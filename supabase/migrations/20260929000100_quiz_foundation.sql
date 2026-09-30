-- QUIZesque content, editorial workflow, and opt-in player history.
-- Question answers are deliberately omitted from all client-readable views.

create extension if not exists pgcrypto with schema extensions;

create type public.content_state as enum ('draft', 'review', 'published', 'retired');
create type public.question_difficulty as enum ('easy', 'medium', 'hard');
create type public.question_skill as enum ('recall', 'vocabulary', 'reasoning', 'application');
create type public.source_grade as enum ('A', 'B', 'C');
create type public.answer_confidence as enum ('unsure', 'leaning', 'certain');
create type public.focus_action as enum ('none', 'clue', 'shield');
create type public.question_report_reason as enum ('wrong_answer', 'outdated', 'ambiguous', 'biased', 'other');
create type public.report_state as enum ('open', 'triaged', 'resolved', 'rejected');

create function public.touch_updated_at()
returns trigger
language plpgsql
set search_path = ''
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

create table public.quiz_categories (
    id text primary key check (id ~ '^[a-z][a-z0-9_-]*$'),
    title text not null,
    description text not null default '',
    sort_order smallint not null default 0,
    is_active boolean not null default true,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table public.quiz_topics (
    id uuid primary key default gen_random_uuid(),
    category_id text not null references public.quiz_categories(id),
    parent_id uuid references public.quiz_topics(id) on delete restrict,
    slug text not null unique check (slug ~ '^[a-z0-9]+([._-][a-z0-9]+)*$'),
    title text not null,
    description text not null default '',
    is_active boolean not null default true,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    check (parent_id is null or parent_id <> id),
    unique (id, category_id)
);
create index quiz_topics_category_idx on public.quiz_topics(category_id, is_active, title);

create table public.question_packs (
    id uuid primary key default gen_random_uuid(),
    slug text not null unique check (slug ~ '^[a-z0-9]+([_-][a-z0-9]+)*$'),
    title text not null,
    description text not null default '',
    version integer not null default 1 check (version > 0),
    locale text not null default 'en-IN',
    state public.content_state not null default 'draft',
    published_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- A question UUID is stable; each editorial change creates a new version row.
create table public.quiz_questions (
    id uuid not null default gen_random_uuid(),
    version integer not null default 1 check (version > 0),
    category_id text not null references public.quiz_categories(id),
    topic_id uuid not null references public.quiz_topics(id),
    locale text not null default 'en-IN',
    prompt text not null check (char_length(prompt) between 10 and 1200),
    options text[] not null check (cardinality(options) = 4),
    correct_index smallint not null check (correct_index between 0 and 3),
    explanation text not null check (char_length(explanation) between 50 and 500),
    source_url text not null check (source_url ~ '^https?://'),
    source_url_secondary text check (source_url_secondary is null or source_url_secondary ~ '^https?://'),
    source_grade public.source_grade not null,
    source_checked_at date not null,
    difficulty public.question_difficulty not null,
    skill public.question_skill not null,
    tags text[] not null default '{}',
    time_sensitive boolean not null default false,
    state public.content_state not null default 'draft',
    dispute_count integer not null default 0 check (dispute_count >= 0),
    reviewed_by uuid references auth.users(id) on delete set null,
    reviewed_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    primary key (id, version),
    foreign key (topic_id, category_id)
        references public.quiz_topics(id, category_id) on delete restrict,
    check (array_position(options, null) is null),
    check (source_grade <> 'C' or source_url_secondary is not null),
    check (state <> 'published' or (reviewed_at is not null and reviewed_by is not null))
);
create index quiz_questions_published_lookup_idx
    on public.quiz_questions(category_id, topic_id, difficulty, locale)
    where state = 'published';
create index quiz_questions_tags_idx on public.quiz_questions using gin(tags);

create table public.question_pack_items (
    pack_id uuid not null references public.question_packs(id) on delete restrict,
    question_id uuid not null,
    question_version integer not null,
    position smallint not null check (position > 0),
    created_at timestamptz not null default now(),
    primary key (pack_id, question_id, question_version),
    unique (pack_id, position),
    foreign key (question_id, question_version)
        references public.quiz_questions(id, version) on delete restrict
);
create index question_pack_items_question_idx
    on public.question_pack_items(question_id, question_version);

-- Translations preserve the reviewed source revision and keep the answer index aligned.
create table public.question_translations (
    question_id uuid not null,
    question_version integer not null,
    locale text not null,
    prompt text not null,
    options text[] not null check (cardinality(options) = 4),
    explanation text not null,
    state public.content_state not null default 'draft',
    reviewed_by uuid references auth.users(id) on delete set null,
    reviewed_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    primary key (question_id, question_version, locale),
    foreign key (question_id, question_version)
        references public.quiz_questions(id, version) on delete restrict,
    check (locale <> ''),
    check (state <> 'published' or (reviewed_at is not null and reviewed_by is not null))
);

create table public.user_profiles (
    id uuid primary key references auth.users(id) on delete cascade,
    display_name text check (display_name is null or char_length(display_name) <= 80),
    preferred_locale text not null default 'en-IN',
    cloud_sync_enabled boolean not null default false,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create function public.create_user_profile()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
    insert into public.user_profiles(id) values (new.id) on conflict (id) do nothing;
    return new;
end;
$$;
create trigger on_auth_user_created_profile
    after insert on auth.users
    for each row execute function public.create_user_profile();

create table public.quiz_runs (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references auth.users(id) on delete cascade,
    category_id text not null references public.quiz_categories(id),
    pack_id uuid references public.question_packs(id) on delete set null,
    locale text not null default 'en-IN',
    mode text not null default 'timed' check (mode in ('timed', 'practice', 'expedition', 'review')),
    started_at timestamptz not null default now(),
    completed_at timestamptz,
    score integer not null default 0,
    total_questions smallint not null default 0 check (total_questions between 0 and 100),
    focus_used smallint not null default 0 check (focus_used between 0 and 100),
    app_version text,
    created_at timestamptz not null default now(),
    check (completed_at is null or completed_at >= started_at)
);
create index quiz_runs_user_recent_idx on public.quiz_runs(user_id, started_at desc);

create table public.quiz_run_answers (
    id uuid primary key default gen_random_uuid(),
    run_id uuid not null references public.quiz_runs(id) on delete cascade,
    question_id uuid not null,
    question_version integer not null,
    selected_index smallint check (selected_index is null or selected_index between 0 and 3),
    is_correct boolean not null default false,
    confidence public.answer_confidence not null default 'unsure',
    focus_action public.focus_action not null default 'none',
    answered_at timestamptz not null default now(),
    foreign key (question_id, question_version)
        references public.quiz_questions(id, version) on delete restrict,
    unique (run_id, question_id, question_version)
);
create index quiz_run_answers_run_idx on public.quiz_run_answers(run_id);
create index quiz_run_answers_question_idx on public.quiz_run_answers(question_id, question_version);

create table public.question_reports (
    id uuid primary key default gen_random_uuid(),
    question_id uuid not null,
    question_version integer not null,
    reporter_id uuid references auth.users(id) on delete set null,
    reason public.question_report_reason not null,
    details text check (details is null or char_length(details) <= 2000),
    state public.report_state not null default 'open',
    triage_note text,
    created_at timestamptz not null default now(),
    resolved_at timestamptz,
    foreign key (question_id, question_version)
        references public.quiz_questions(id, version) on delete restrict
);
create index question_reports_open_idx on public.question_reports(created_at)
    where state in ('open', 'triaged');

create function public.count_question_report()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
    update public.quiz_questions
       set dispute_count = dispute_count + 1
     where id = new.question_id and version = new.question_version;
    return new;
end;
$$;
create trigger on_question_report_created
    after insert on public.question_reports
    for each row execute function public.count_question_report();

create trigger quiz_categories_touch_updated_at before update on public.quiz_categories
    for each row execute function public.touch_updated_at();
create trigger quiz_topics_touch_updated_at before update on public.quiz_topics
    for each row execute function public.touch_updated_at();
create trigger question_packs_touch_updated_at before update on public.question_packs
    for each row execute function public.touch_updated_at();
create trigger quiz_questions_touch_updated_at before update on public.quiz_questions
    for each row execute function public.touch_updated_at();
create trigger question_translations_touch_updated_at before update on public.question_translations
    for each row execute function public.touch_updated_at();
create trigger user_profiles_touch_updated_at before update on public.user_profiles
    for each row execute function public.touch_updated_at();

-- Client content endpoint: never returns correct_index or editorial-only metadata.
create view public.published_quiz_content
with (security_invoker = true)
as
select q.id as question_id,
       q.version,
       p.id as pack_id,
       pi.position as pack_position,
       p.slug as pack_slug,
       p.title as pack_title,
       q.category_id,
       q.topic_id,
       t.slug as topic_slug,
       t.title as topic_title,
       q.locale,
       q.prompt,
       q.options,
       q.explanation,
       q.source_url,
       q.source_url_secondary,
       q.source_grade,
       q.source_checked_at,
       q.difficulty,
       q.skill,
       q.tags,
       q.time_sensitive
from public.quiz_questions q
join public.question_pack_items pi on pi.question_id = q.id and pi.question_version = q.version
join public.question_packs p on p.id = pi.pack_id
join public.quiz_topics t on t.id = q.topic_id
where q.state = 'published'
  and p.state = 'published'
  and t.is_active;

create view public.my_topic_progress
with (security_invoker = true)
as
select r.user_id,
       q.topic_id,
       t.slug as topic_slug,
       t.title as topic_title,
       count(*)::integer as attempts,
       count(*) filter (where a.is_correct)::integer as correct,
       round(100.0 * count(*) filter (where a.is_correct) / nullif(count(*), 0), 1) as accuracy_percent,
       count(*) filter (where a.confidence = 'certain')::integer as certain_attempts,
       count(*) filter (where a.confidence = 'certain' and a.is_correct)::integer as certain_correct
from public.quiz_run_answers a
join public.quiz_runs r on r.id = a.run_id
join public.quiz_questions q on q.id = a.question_id and q.version = a.question_version
join public.quiz_topics t on t.id = q.topic_id
group by r.user_id, q.topic_id, t.slug, t.title;

-- This RPC reveals the answer only after the client submits an option.
create function public.check_quiz_answer(
    p_question_id uuid,
    p_version integer,
    p_selected_index smallint
)
returns table (is_correct boolean, correct_index smallint, explanation text)
language sql
stable
security definer
set search_path = ''
as $$
    select p_selected_index = q.correct_index, q.correct_index, q.explanation
    from public.quiz_questions q
    where q.id = p_question_id
      and q.version = p_version
      and p_selected_index between 0 and 3
      and q.state = 'published'
      and exists (
          select 1
          from public.question_pack_items pi
          join public.question_packs p on p.id = pi.pack_id
          where pi.question_id = q.id
            and pi.question_version = q.version
            and p.state = 'published'
      )
$$;

-- A timed-out round still owes the player the answer. Same visibility rules as
-- check_quiz_answer, minus the submitted option: the client only calls this once
-- the round is already over, so it cannot be used to fish for answers early.
create function public.reveal_quiz_answer(
    p_question_id uuid,
    p_version integer
)
returns table (correct_index smallint, explanation text)
language sql
stable
security definer
set search_path = ''
as $$
    select q.correct_index, q.explanation
    from public.quiz_questions q
    where q.id = p_question_id
      and q.version = p_version
      and q.state = 'published'
      and exists (
          select 1
          from public.question_pack_items pi
          join public.question_packs p on p.id = pi.pack_id
          where pi.question_id = q.id
            and pi.question_version = q.version
            and p.state = 'published'
      )
$$;

-- Lock down direct access first. Content is read through constrained views.
revoke all on public.quiz_categories, public.quiz_topics, public.question_packs,
    public.quiz_questions, public.question_pack_items, public.question_translations, public.user_profiles,
    public.quiz_runs, public.quiz_run_answers, public.question_reports from anon, authenticated;
revoke all on public.published_quiz_content, public.my_topic_progress from anon, authenticated;

grant select on public.published_quiz_content to anon, authenticated;
grant select on public.quiz_categories to anon, authenticated;
grant select on public.quiz_topics to anon, authenticated;
grant select on public.question_packs to anon, authenticated;
grant select on public.question_pack_items to anon, authenticated;
grant select (id, version, category_id, topic_id, locale, prompt, options,
    explanation, source_url, source_url_secondary, source_grade, source_checked_at,
    difficulty, skill, tags, time_sensitive, state) on public.quiz_questions to anon, authenticated;

grant select, insert, update on public.user_profiles to authenticated;
grant select, insert, update, delete on public.quiz_runs to authenticated;
grant select, insert, update, delete on public.quiz_run_answers to authenticated;
grant select on public.my_topic_progress to authenticated;
grant insert (question_id, question_version, reporter_id, reason, details)
    on public.question_reports to authenticated;

grant usage on type public.content_state, public.question_difficulty, public.question_skill,
    public.source_grade, public.answer_confidence, public.focus_action,
    public.question_report_reason, public.report_state to anon, authenticated;
revoke all on function public.check_quiz_answer(uuid, integer, smallint) from public;
grant execute on function public.check_quiz_answer(uuid, integer, smallint) to anon, authenticated;
revoke all on function public.reveal_quiz_answer(uuid, integer) from public;
grant execute on function public.reveal_quiz_answer(uuid, integer) to anon, authenticated;

alter table public.quiz_categories enable row level security;
alter table public.quiz_topics enable row level security;
alter table public.question_packs enable row level security;
alter table public.quiz_questions enable row level security;
alter table public.question_pack_items enable row level security;
alter table public.question_translations enable row level security;
alter table public.user_profiles enable row level security;
alter table public.quiz_runs enable row level security;
alter table public.quiz_run_answers enable row level security;
alter table public.question_reports enable row level security;

create policy "active categories are readable" on public.quiz_categories
    for select to anon, authenticated using (is_active);
create policy "active topics are readable" on public.quiz_topics
    for select to anon, authenticated using (is_active);
create policy "published packs are readable" on public.question_packs
    for select to anon, authenticated using (state = 'published');
create policy "published questions back the content view" on public.quiz_questions
    for select to anon, authenticated using (state = 'published');
create policy "published pack mappings are readable" on public.question_pack_items
    for select to anon, authenticated using (exists (
        select 1 from public.question_packs p
        where p.id = pack_id and p.state = 'published'
    ));
create policy "published translations are readable" on public.question_translations
    for select to anon, authenticated using (state = 'published');

create policy "users read own profile" on public.user_profiles
    for select to authenticated using ((select auth.uid()) = id);
create policy "users update own profile" on public.user_profiles
    for update to authenticated using ((select auth.uid()) = id)
    with check ((select auth.uid()) = id);

create policy "users read own runs" on public.quiz_runs
    for select to authenticated using ((select auth.uid()) = user_id);
create policy "users create own runs" on public.quiz_runs
    for insert to authenticated with check ((select auth.uid()) = user_id and exists (
        select 1 from public.user_profiles p
        where p.id = user_id and p.cloud_sync_enabled
    ));
create policy "users update own runs" on public.quiz_runs
    for update to authenticated using ((select auth.uid()) = user_id)
    with check ((select auth.uid()) = user_id and exists (
        select 1 from public.user_profiles p
        where p.id = user_id and p.cloud_sync_enabled
    ));
create policy "users delete own runs" on public.quiz_runs
    for delete to authenticated using ((select auth.uid()) = user_id);

create policy "users read answers in own runs" on public.quiz_run_answers
    for select to authenticated using (exists (
        select 1 from public.quiz_runs r where r.id = run_id and r.user_id = (select auth.uid())
    ));
create policy "users create answers in own runs" on public.quiz_run_answers
    for insert to authenticated with check (exists (
        select 1 from public.quiz_runs r
        join public.user_profiles p on p.id = r.user_id and p.cloud_sync_enabled
        where r.id = run_id and r.user_id = (select auth.uid())
    ));
create policy "users update answers in own runs" on public.quiz_run_answers
    for update to authenticated using (exists (
        select 1 from public.quiz_runs r where r.id = run_id and r.user_id = (select auth.uid())
    )) with check (exists (
        select 1 from public.quiz_runs r where r.id = run_id and r.user_id = (select auth.uid())
    ));
create policy "users delete answers in own runs" on public.quiz_run_answers
    for delete to authenticated using (exists (
        select 1 from public.quiz_runs r where r.id = run_id and r.user_id = (select auth.uid())
    ));

create policy "users submit own question reports" on public.question_reports
    for insert to authenticated with check ((select auth.uid()) = reporter_id);

insert into public.quiz_categories(id, title, description, sort_order) values
    ('gk', 'General Knowledge', 'World, science, history and more', 1),
    ('word', 'Word / Vocabulary', 'Meanings, synonyms and antonyms', 2),
    ('riddle', 'Riddles', 'Brainteasers, puzzles and wordplay', 3)
on conflict (id) do update set
    title = excluded.title,
    description = excluded.description,
    sort_order = excluded.sort_order,
    updated_at = now();

insert into public.quiz_topics(category_id, slug, title, description) values
    ('gk', 'gk.world', 'World', 'Countries, geography and global institutions'),
    ('gk', 'gk.science', 'Science', 'Natural and applied sciences'),
    ('gk', 'gk.history', 'History', 'Historical people, places and events'),
    ('word', 'word.meanings', 'Meanings', 'Word meanings and usage'),
    ('word', 'word.synonyms-antonyms', 'Synonyms and Antonyms', 'Word relationships'),
    ('word', 'word.spelling-idioms', 'Spelling and Idioms', 'Spelling, phrases and idiomatic usage'),
    ('riddle', 'riddle.classic', 'Classic Riddles', 'What am I brainteasers and their answers'),
    ('riddle', 'riddle.logic', 'Logic Puzzles', 'Deduction, patterns and number play'),
    ('riddle', 'riddle.wordplay', 'Wordplay', 'Puns, anagrams and trick wording'),
    ('riddle', 'riddle.lateral', 'Lateral Thinking', 'Unexpected angles and trick questions')
on conflict (slug) do nothing;
