# QuizMaster — Android Quiz App

Native Android quiz app (Kotlin + Jetpack Compose + Material 3) with three segments:

1. **GK** — General Knowledge (10 questions)
2. **Word** — Vocabulary: meanings, synonyms, antonyms, spelling, idioms (10 questions)
3. **Sports** — Cricket, football, tennis, Olympics & more (10 questions)

## Features
- Animated **Attatva Games** studio splash (emblem reveal, letter animation, particles, loading bar)
- Home screen with 3 category cards
- Quiz screen: 30-second timer per question, progress bar, live score
- Instant feedback with explanation after each answer
- Result screen: score, percentage, grade + full answer review
- Play Again / Home navigation, shuffled questions per run

## Project structure
```
app/src/main/java/com/example/quizmaster/
  MainActivity.kt          # NavHost: home -> quiz/{id} -> result
  model/Models.kt          # QuizCategory, Question, AnsweredQuestion
  data/QuestionBank.kt     # 30 questions (10 per category)
  ui/QuizViewModel.kt      # quiz state machine
  ui/screens/HomeScreen.kt
  ui/screens/QuizScreen.kt
  ui/screens/ResultScreen.kt
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

## Add / edit questions
Edit `data/QuestionBank.kt` — each `Question` needs 4 options, a `correctIndex` (0–3),
and an explanation shown after answering.
