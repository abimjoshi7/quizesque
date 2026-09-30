# Supabase database foundation

The first migration is `supabase/migrations/20260929000100_quiz_foundation.sql`.
It sets up the content model and the optional, account-based learning history
described in the product roadmap, and seeds the three segments the app ships
with: `gk`, `word` and `riddle` (each with its own topic tree).

## Included

- Categories, hierarchical topics, packs, versioned questions, editorial review
  state, source metadata, difficulty, skills, tags, and translations.
- A client content view that omits the answer index, plus an answer-check RPC
  for a submitted answer and a reveal RPC for timed-out rounds.
- User profiles, opt-in synced quiz runs and answers, and per-topic progress.
- Authenticated question reports for content correction triage.
- Row-level security and explicit grants. User-owned history is scoped to the
  signed-in user; public clients can read only active/published content.

Question content is writeable only with trusted database credentials. Never
ship a `service_role` key in the Android app. Cloud history is not enabled in
the current app flow; sync should only be added behind an explicit opt-in.

## How the Android app uses it

`data/ContentRepository.kt` is the only caller, with the publishable/anon key:

| Call | Purpose |
| --- | --- |
| `GET published_quiz_content?category_id=eq.{segment}` | Questions for one segment, `locale=en-IN`, ordered by `pack_position`, capped at 10 usable rows. Columns: `question_id`, `version`, `prompt`, `options`, `explanation` — never `correct_index`. |
| `POST rpc/check_quiz_answer` | Grades the option the player just locked in; returns `is_correct`, `correct_index`, `explanation`. |
| `POST rpc/reveal_quiz_answer` | Fetches the answer for a round that ran out of time. |

Everything else falls back to the bundled `QuestionBank`: no credentials, an
empty view, a timeout (8s fetch / 6s RPC), or a failed check all leave the run
playable. A failed check marks the round `NOT VERIFIED` rather than guessing.

## Apply locally

With Docker running:

```sh
supabase start
supabase db reset
supabase db lint --local --level error
```

`db reset` rebuilds the local Supabase database from all migrations and
`supabase/seed.sql`. It is destructive to the local Supabase database.

## Apply to a remote project

The project URL and publishable/anon key configured for Android do not authorize
schema changes. After confirming the target Supabase project is the intended
database, authenticate the CLI, link it, inspect migration status, then push:

```sh
supabase login
supabase link --project-ref YOUR_PROJECT_REF
supabase migration list
supabase db push --dry-run
supabase db push
```

Review the dry run and any existing remote schema before applying the migration.
Do not use the Android key or a `service_role` key as the database password.

Verify the push in the SQL editor (or `supabase db dump --data-only`):

```sql
select id, title, sort_order from public.quiz_categories order by sort_order;
select slug from public.quiz_topics order by category_id, slug;
select proname from pg_proc where pronamespace = 'public'::regnamespace
  and proname in ('check_quiz_answer', 'reveal_quiz_answer');
```

Then check the client endpoint with the same key the app uses:

```sh
curl -s "$SUPABASE_URL/rest/v1/published_quiz_content?select=question_id,category_id,pack_position&limit=5" \
  -H "apikey: $SUPABASE_ANON_KEY" \
  -H "Authorization: Bearer $SUPABASE_ANON_KEY"
```

An empty JSON array means the schema is live but no content has been published
yet — the app plays its bundled questions until that changes.

## First content release

The app only sees a question once it sits in a **published** pack, its topic is
active, and the row itself is `published` — which requires a reviewer and a
review timestamp. Factual questions additionally need a source URL, source grade
and checked date; grade `C` also needs a secondary source. Imported explanations
must be 50–500 characters (the bundled ones are often shorter), and `category_id`
must match the topic's category.

Template (run as a trusted role, e.g. in the SQL editor):

```sql
-- 1) A pack is what the app downloads as one unit.
insert into public.question_packs (slug, title, description, version, locale, state, published_at)
values ('launch-v1', 'Launch mix', 'First published run', 1, 'en-IN', 'published', now());

-- 2) A question, filed under a topic of the same segment. Still in review.
insert into public.quiz_questions (
    version, category_id, topic_id, locale, prompt, options, correct_index,
    explanation, source_url, source_grade, source_checked_at, difficulty, skill, state
)
select 1, 'riddle', t.id, 'en-IN',
       'I speak without a mouth and hear without ears. What am I?',
       array['Echo', 'Shadow', 'Smoke', 'Whistle'], 0,
       'An echo carries sound back to you without needing a mouth or ears of its own.',
       'https://en.wikipedia.org/wiki/Echo', 'B', current_date, 'easy', 'reasoning',
       'review'
from public.quiz_topics t where t.slug = 'riddle.classic';

-- 3) Put it in the pack at position 1 (the app orders runs by pack_position).
insert into public.question_pack_items (pack_id, question_id, question_version, position)
select p.id, q.id, q.version, 1
from public.question_packs p
join public.quiz_questions q on q.prompt like 'I speak without a mouth%'
where p.slug = 'launch-v1';

-- 4) Publish it. Replace REVIEWER_UUID with the auth.users id of the account
--    that reviewed the question (Dashboard > Authentication > Users).
update public.quiz_questions
   set state = 'published', reviewed_by = 'REVIEWER_UUID'::uuid, reviewed_at = now()
 where prompt like 'I speak without a mouth%';
```

Repeat per question, then re-run the `curl` check above. Keeping every segment
(`gk`, `word`, `riddle`) at ten published questions matches the "ten rounds each"
promise on the home screen; the app takes at most ten per run anyway.

## Not covered yet

- Player history (`quiz_runs`, `quiz_run_answers`) is schema-ready but the app
  does not write to it; that lands behind an explicit sync opt-in.
- Question reports and translations have no app surface yet.
