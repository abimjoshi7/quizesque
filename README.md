# QUIZesque — Android Quiz App

Native Android quiz app (Kotlin + Jetpack Compose + Material 3) with three segments:

1. **GK** — General Knowledge (10 questions)
2. **Word** — Vocabulary: meanings, synonyms, antonyms, spelling, idioms (10 questions)
3. **Sports** — Cricket, football, tennis, Olympics & more (10 questions)

## Features
- Animated **Attatva Games** studio splash (emblem reveal, letter animation, particles, loading bar)
- **QUIZesque logo** — a geometric Q in the Attatva pill-arc language with the gold bindu,
  plus the `QUIZ`/`esque` wordmark with its hairline under the leading Q
  (`ui/components/QuizesqueLogo.kt`). Vector only, scales 20dp → full splash.
- **Launcher icon** — adaptive icon (API 26+) with a monochrome variant for Android 13
  themed icons, plus legacy PNG mipmaps for API 24–25.
- **Studio boot loader** on the game screen — instrument dial, sweep meter, ping waves,
  orbiting satellites, the Q mark at its core, monospace telemetry + typed arena name
  (`ui/components/StudioBootLoader.kt`). Long-press the round chip to replay it.
- Home screen with 3 category cards
- Quiz screen: 30-second timer dial, per-round progress meter, live score
- Instant feedback with explanation after each answer
- Result screen: score, percentage, grade + full answer review
- Play Again / Home navigation, shuffled questions per run

## Project structure
```
app/src/main/java/com/abimatwork/quizesque/
  MainActivity.kt          # NavHost: home -> quiz/{id} -> result
  model/Models.kt          # QuizCategory, Question, AnsweredQuestion
  data/QuestionBank.kt     # 30 questions (10 per category)
  ui/QuizViewModel.kt      # quiz state machine
  ui/screens/HomeScreen.kt
  ui/screens/QuizScreen.kt
  ui/screens/ResultScreen.kt
  ui/components/StudioBootLoader.kt  # studio boot loader
  ui/components/QuizesqueLogo.kt    # game logo: mark, wordmark, badge, lockups
  ui/theme/Theme.kt
```

## Build & run
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug   # with device/emulator connected
```

Requirements: Android SDK (compileSdk 34, minSdk 24), JDK 17.
`local.properties` already points to `/home/abim/Android/Sdk`.

## Supabase setup

The app includes an optional Supabase client (Auth + PostgREST). The quiz still
works offline when no Supabase settings are present. To configure it, add these
Gradle properties to `~/.gradle/gradle.properties` (or a local, untracked
`gradle.properties`). This checkout also reads `.gradle/gradle.properties`,
which is ignored by Git:

```properties
SUPABASE_URL=https://YOUR_PROJECT.supabase.co
SUPABASE_ANON_KEY=YOUR_PUBLISHABLE_OR_ANON_KEY
```

Sync Gradle after setting them. `SupabaseProvider.client` is then available for
repositories to use; it is `null` while configuration is missing. The client
uses the public app key, so enable Row Level Security and write policies for
every exposed table. Never put a `service_role` key in the Android app. The
database migration and rollout notes are in
[docs/SUPABASE_DATABASE.md](docs/SUPABASE_DATABASE.md). The current app does not
yet fetch remote questions or sync player history.

## Add / edit questions
Edit `data/QuestionBank.kt` — each `Question` needs 4 options, a `correctIndex` (0–3),
and an explanation shown after answering.

## Launcher icon
`res/mipmap-anydpi-v26/ic_launcher.xml` (adaptive) + `res/drawable/ic_launcher_{background,
foreground,monochrome}.xml` (vectors). The legacy PNGs for API 24–25 are generated from the
same geometry — regenerate them if the mark changes:

```bash
python3 tools/make_launcher_icons.py                 # writes the mipmap-* PNGs
python3 tools/make_launcher_icons.py --preview out.png  # large proof sheet
```

## Product direction
See [the QUIZesque product roadmap](docs/PRODUCT_ROADMAP.md) for the proposed
Confidence Plays mechanic, learning-first expansion phases, and product guardrails.
