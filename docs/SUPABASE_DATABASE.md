# Supabase database foundation

The first migration is `supabase/migrations/20260929000100_quiz_foundation.sql`.
It sets up the content model and the optional, account-based learning history
described in the product roadmap. The bundled question bank remains the offline
source of truth until a content download repository is added to Android.

## Included

- Categories, hierarchical topics, packs, versioned questions, editorial review
  state, source metadata, difficulty, skills, tags, and translations.
- A client content view that omits the answer index, plus an answer-check RPC
  for a submitted answer.
- User profiles, opt-in synced quiz runs and answers, and per-topic progress.
- Authenticated question reports for content correction triage.
- Row-level security and explicit grants. User-owned history is scoped to the
  signed-in user; public clients can read only active/published content.

Question content is writeable only with trusted database credentials. Never
ship a `service_role` key in the Android app. Cloud history is not enabled in
the current app flow; sync should only be added behind an explicit opt-in.

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

## First content release

Create a published `question_pack`, then add reviewed question revisions whose
category matches their topic. A row can become `published` only after it has a
reviewer and review timestamp, and all factual questions need a source URL,
source grade, and checked date. The Android app currently does not fetch this
view or submit answers to the RPC; that repository and a content import process
are the next integration steps.
