# QUIZesque: product direction and expansion plan
**Version:** 1.1.0 — see [Changelog & Audit Trail](#changelog--audit-trail) for what changed.

## Product promise

**QUIZesque turns useful knowledge into a tactical advantage.** Players choose
what to tackle, decide how much confidence to put behind an answer, and leave
each round with a short, verifiable explanation they can use next time.

The signature loop is **Choose → Commit → Reveal → Learn → Adapt**:

1. Choose an arena or topic route.
2. Answer and mark confidence before the reveal.
3. Use a limited strategic action when the stakes justify it.
4. See the answer, a concise explanation, and its source.
5. Review weak topics and choose a smarter route next run.

This makes the game about judgment and learning, not only recall speed or a
large question count. Timers can add tension, but should never be the only way
to distinguish a strong player.

## Signature mechanic: Confidence Plays

Before locking an answer, the player marks **Unsure**, **Leaning**, or
**Certain**. Each run also grants a small number of **Focus** plays. A Focus
play can be spent to reveal a useful clue or to protect a low-confidence answer
from a score penalty. Players decide when information is worth spending and
when to commit without help.

The scoring should reward correct answers and calibrated confidence. A wrong
high-confidence answer should teach more than it punishes; it should never erase
learning progress. Explanations remain visible after every answer, including
when Focus is used.

## Expansion roadmap

### 1. Make the question set trustworthy

- Tag every question by topic, difficulty, and skill (recall, vocabulary,
  reasoning, or application).
- Add a concise explanation and a source reference for factual claims; record
  when time-sensitive facts were checked.
- Add editorial review fields and flag disputed or time-sensitive questions.
- Keep the first content set playable offline.

**Ready when:** every shipped question has a clear answer, useful explanation,
topic tag, and reviewable source.

### 2. Ship the QUIZesque Expedition

- Replace a flat list of ten questions with a short route through connected
  topics. Let players pick between two next stops so each run has a strategic
  shape.
- Introduce Confidence Plays with a small, fixed Focus budget per run.
- Show the reason behind the answer immediately, then save a compact Field Note
  to the run recap.
- Keep a no-timer practice option for thoughtful play and accessibility.

**Ready when:** a player can explain the tradeoff behind a route choice or Focus
play, and still learns from every miss.

### 3. Turn results into a personal knowledge map

- Track accuracy and confidence calibration by topic, not just total score.
- Build a review deck from missed, skipped, and low-confidence questions.
- Revisit those questions after a delay with a fresh prompt or related example.
- Let players see why a topic is marked “practising” or “solid” and reset local
  history whenever they choose.

**Ready when:** a returning player can identify what improved and what to
practise next without needing a streak or leaderboard.

### 4. Add replayable challenges

- Publish a rotating daily Expedition with the same route and question order for
  everyone that day.
- Add optional asynchronous friend challenges using a shareable seed, so both
  players get the same run without needing live matchmaking.
- Compare score, accuracy, and confidence calibration separately; do not rank
  players by speed alone.
- Add more arenas only when each has enough reviewed questions to stay varied.

**Ready when:** challenge runs are reproducible, fair, and still include the
same explanations and review notes as solo play.

## Product guardrails

- **Inform first:** every answer reveal explains why; wrong answers are useful
  feedback, never a dead end.
- **Strategy over reflex:** time is an optional pressure setting, not the core
  measure of knowledge.
- **Show uncertainty honestly:** source time-sensitive facts and label claims
  that may change.
- **No grind required:** avoid energy gates, punitive streak loss, and progress
  systems that reward repeated guessing.
- **Private by default:** keep learning history on device until a player opts
  into sync or a social feature.
- **Accessible choices:** support practice without a timer, readable contrast,
  and actions that do not depend on color alone.

## What to measure

- **Learning:** repeat-question accuracy after a delay and improvement on weak
  topics.
- **Strategy:** whether Focus use and confidence marks correlate with better
  decisions over time.
- **Content quality:** question correction rate, source coverage, and player
  reports of ambiguous answers.
- **Enjoyment:** voluntary return to a new Expedition and completion without
  relying on streak pressure.

Treat these as product signals, not targets to manipulate. A feature should ship
only if it improves learning or meaningful choice without making the game less
welcoming.

## Recommendations: end-user value accelerators (added v1.1.0)

Focus: deliver visible value in the first 60 seconds and on every return,
without changing the Choose → Commit → Reveal → Learn → Adapt promise.

### 5. Close the current-app gaps first (P0, low effort)

Current app (`app/src/main/java/com/abimatwork/quizesque/model/Models.kt:14`,
`data/QuestionBank.kt:7`, `ui/QuizViewModel.kt:14`) ships 30 questions with
`question/options/correctIndex/explanation` only — no topic tags, sources,
difficulty, timer choice, or review deck. Highest value-per-effort:

- **No-timer practice toggle on Home + Quiz.** Roadmap §2 already promises it,
  but the current `QuizScreen.kt:62` hard-codes `TIME_PER_QUESTION = 30`. Ship
  the toggle before Expedition — it is an accessibility win and unblocks
  thoughtful play.
- **Field Notes export.** The run recap in `ui/screens/ResultScreen.kt:36`
  already lists answers but drops `explanation`. Save Q + correct answer +
  explanation as a shareable text card. Cost: one composable. Value: the player
  leaves with something usable outside the app.
- **Instant "Retry misses" button on Result.** Reuses `answers: List<AnsweredQuestion>`
  already in memory. No persistence needed for v1. Directly serves roadmap §3
  intent with ~1 day of work.
- **Difficulty ramp within 10-Q runs.** Sort first 3 easy / middle 4 medium /
  last 3 hard once tags exist. Prevents early discouragement, no new screens.

### 6. Knowledge Map that earns return visits (P1)

- **3-state topic status with reasons.** Roadmap §3 says show why a topic is
  "practising" vs "solid" — specify the rule now (e.g. solid = ≥80% over last
  10 attempts + calibrated confidence; practising = else; show `n/10` counts).
  Vague labels erode trust.
- **Spaced review, not infinite deck.** Cap review to 5 cards/day, ordered by
  oldest miss + lowest confidence. Respects "No grind required" guardrail and
  keeps sessions short.
- **Calibration score, not just accuracy.** Show "You say Certain and are right
  X% of the time" on Result. This is the unique Confidence Plays payoff —
  no competitor shows this well.

## Recommendations: guardrail-compliant revenue (added v1.1.0)

Principle: charge for **content depth, convenience, and cosmetics** — never for
fairness, explanations, or recovery from mistakes. All proposals checked
against the Product guardrails above.

### 7. What to sell (P1–P2 order)

1. **Expedition Packs (one-time purchase, primary revenue).** E.g. Exam Prep
   (UPSC/SSC-style GK), Word Mastery 500, Cricket Deep-Cut. Each pack = 200+
   reviewed questions with sources. Why it works: aligns with §1 trust work,
   offline-first, Indian exam market willingness to pay for verified content.
   Free app keeps current 30 Q + daily Expedition; packs expand arenas per §4
   rule ("only when reviewed").
2. **Pro convenience (one-time or yearly, no subscription trap).**
   - Unlimited custom friend-challenge seeds + rematch (§4 async challenges
     stay free at 1/day; unlimited is Pro).
   - Full Expedition archive + review-deck history beyond 30 days.
   - Field Notes PDF export + share packs.
   - Never gate: explanations, Focus plays, practice mode, or retry-misses.
3. **Cosmetics only (no gameplay effect).** Boot-loader skins, arena themes,
   badge frames in `ui/components/QuizesqueLogo.kt` / `StudioBootLoader.kt`.
   Fits the existing studio aesthetic; zero fairness risk.
4. **Classroom / coaching license (B2B, P2).** Teacher creates seed, students
   play same run, teacher sees aggregate weak topics (opt-in only). Keeps
   "Private by default" — per-student data stays on device unless shared.

### 8. What NOT to sell (guardrail violations)

- No energy gates, extra-timer purchases, or paid Focus refills — violates
  "No grind required" + "Strategy over reflex."
- No pay-to-reveal-answers or paid score multipliers — destroys trust signal.
- No interstitial ads mid-run. If ads ever tested: Result screen only,
  opt-in rewarded (e.g. watch to unlock a bonus archaeology pack), never
  auto-play, never during Choose → Commit.
- No streak-punishment or loot-box question unlocks.

### 9. Pricing + validation suggestions

- India-first pricing: packs ₹99–249 one-time; Pro ₹499/yr. Validate with 2
  fake-door tests (packs page → waitlist) before building billing.
- Measure willingness before building: pack pre-orders, challenge-seed share
  rate, Field Notes export rate — if export rate is low, PDF-Pro has no market.
- Keep APK small: packs as on-demand downloads, base app stays offline with
  the first 30–100 Q.

## Updated measures (additions for v1.1.0)

Keep all existing signals. Add:

- **Value:** D1/D7 return without notification, Retry-misses tap rate,
  Field Notes share rate, practice-mode adoption.
- **Content:** % questions with source + last-checked date, report rate per
  1,000 plays.
- **Revenue (non-manipulative):** free→pack conversion per arena, Pro trial
  → paid, refund rate + reason. Never optimise time-spent or Focus burn.

## Suggested build order (value × revenue)

- **P0 now:** practice toggle, Retry-misses, keep explanations in Result.
- **P1 next:** question tagging + sources schema (unblocks packs), Confidence
  Plays + Focus budget, calibration display.
- **P2 after trust + loop proven:** Daily Expedition, paid packs, friend seeds,
  cosmetics, classroom pilot.

## Open questions

- Which exam segment converts first (GK vs Words vs Riddles)? Instrument pack
  page views before committing content budget.
- Is Focus-as-clue or Focus-as-shield more understood? A/B test wording, not
  drop rates.

## Changelog & Audit Trail

- **v1.0.0 (baseline):** Original promise, Confidence Plays, roadmap §1–§4,
  guardrails, measures. No version header in file.
- **v1.1.0 — 2026-09-29:** Added version header; added §5–§9 (value
  accelerators, revenue plan, anti-patterns, pricing, build order, open
  questions, extended measures). No changes to §§1–4 promise/mechanics text.
  Reason: align current plan with end-user value in first session and
  guardrail-compliant monetization. Author: OpenCode agent review vs current
  `Question` model (no tags/sources) and hard-coded 30s timer. Status:
  proposal — needs owner approval before P0 build.
- **v1.2.0 — 2026-09-29:** Added §10–§13 (Content Ops, Platform Strategy,
  Launch Checklist, Team Rituals) and Appendices A–C (Seeding Spec, Scoring
  Formula, P0 Sprint Plan). Reason: operationalize content pipeline, de-risk
  platform expansion, define launch gates, and give the team a 2-week P0
  sprint with demoable outputs. Author: OpenCode agent. Status: proposal —
  needs owner approval before P0 build.

## 10. Content Operations & Editorial Pipeline

### 10.1 Question Lifecycle

Every question moves through a defined pipeline before shipping:

| Stage | Owner | Gate Criteria | Tooling |
|-------|-------|---------------|---------|
| **Draft** | Content writer | Question + 4 options + correctIndex + explanation + source URL | Google Sheet / CMS |
| **Fact-check** | Editor (domain) | Primary source verified; time-sensitive facts tagged with `checked_date` | Checklist + source linker |
| **Editorial review** | Lead editor | Tags assigned (topic, difficulty, skill); no ambiguous wording; style guide pass | Review UI with diff |
| **Tagged & locked** | Content lead | Schema validation passes (see §10.3); added to pack manifest | CI pipeline |
| **Published** | Release | Pack version bumped; changelog entry; offline bundle built | Wrangler / Gradle task |
| **Monitored** | Ops | Correction rate < 2% per 1k plays; player report rate < 0.5% | Analytics dashboard |
| **Retired / Updated** | Content lead | New version created; old version preserved for seeded challenges | Versioned question store |

**Rule:** No question ships without passing Fact-check + Editorial review. Hotfixes (typo, broken source) skip to Tagged but require post-hoc review within 48h.

### 10.2 Source Grading

| Grade | Examples | Required For |
|-------|----------|--------------|
| **A — Primary / Official** | Govt. gazettes, RBI circulars, ISO standards, peer-reviewed papers, official sport governing bodies | Time-sensitive facts, legal/regulatory, medical, financial |
| **B — Reputable Secondary** | Established news (Reuters, The Hindu, BBC), standard textbooks, recognized encyclopedias | General knowledge, historical facts, vocabulary etymology |
| **C — Community / Aggregated** | Wikipedia (with inline cite), Quizlet sets, forum consensus | Cultural trivia, colloquial usage, "pub quiz" style — *must be corroborated* |

**Policy:** Every question records `source_grade` (A/B/C) and `source_url`. Grade C questions require a second independent source or explicit "uncertain" flag in explanation.

### 10.3 Question Schema (v2)

```kotlin
data class Question(
  val id: String,                    // UUID v4, stable across versions
  val version: Int,                  // increments on any content change
  val prompt: String,                // the question text
  val options: List<String>,         // 4 options, shuffled at render
  val correctIndex: Int,             // 0-3, index into `options`
  val explanation: String,           // 1-3 sentences, verifiable
  val sourceUrl: String,             // canonical source
  val sourceGrade: SourceGrade,      // A | B | C
  val sourceCheckedAt: Long,         // epoch ms, when fact was verified
  val topic: String,                 // e.g. "indian_polity.fundamental_rights"
  val difficulty: Difficulty,        // EASY | MEDIUM | HARD
  val skill: Skill,                  // RECALL | VOCAB | REASONING | APPLICATION
  val tags: Set<String>,             // free-form: "upsc", "cricket", "etymology"
  val timeSensitive: Boolean,        // true if fact may change (e.g. office-holders)
  val disputeCount: Int,             // player reports, reset on version bump
  val locale: String = "en-IN"       // supports future hi-IN, ta-IN, etc.
)
```

**Validation rules (CI-enforced):**
- `explanation` length 50–500 chars, contains no "correct answer is" tautology
- `sourceUrl` returns 200, content-type text/html or application/pdf
- `topic` matches registry in `topics.yaml` (prevents topic drift)
- `difficulty` distribution per pack: 30% EASY / 50% MEDIUM / 20% HARD ±5%

### 10.4 Dispute & Correction Workflow

1. Player taps "Report" on reveal screen → selects reason (Wrong answer / Outdated / Ambiguous / Biased / Other).
2. Report lands in triage queue (GitHub Issue template / internal board) with: questionId, version, device locale, player's selected answer + confidence.
3. **SLA:** Triage within 48h → assign to domain editor.
4. **Resolution paths:**
   - **Typo / formatting** → hotfix, version+1, push within 24h.
   - **Fact correction** → new version with updated explanation + source, old version archived for seed replay.
   - **Ambiguous wording** → rewrite prompt/options, version+1, keep both versions if seeded challenges used old one.
   - **Rejected** → close with reason shown to reporter (optional in-app toast).
5. **Correction rate metric:** `corrections_published / questions_live` tracked per pack per month.

### 10.5 Content Velocity Targets

| Role | Sustainable Output | Notes |
|------|-------------------|-------|
| Domain writer | 15–20 reviewed questions/week | Includes research + source linking |
| Editor (fact-check) | 40–50 questions/week | Parallelizable across domains |
| Lead editor (review + tag) | 30–40 questions/week | Bottleneck — invest in tooling first |

**Pack launch math:** 200 questions ≈ 2 writer-weeks + 1 editor-week + 3 days lead review. With 2 writers + 1 editor, a pack ships every 3–4 weeks. First 3 packs (GK, Word, Cricket) → 9–12 weeks from P1 start.

### 10.6 Localization Pipeline (hi-IN v1)

- Translate prompt + options + explanation (not source URL).
- Separate `QuestionLocale` table keyed by `questionId + locale`.
- Same schema, same versioning. Fact-check done on English master; localization QA checks cultural fit.
- Ship hi-IN as free update to base app; no separate pack.

---

## 11. Platform Strategy

### 11.1 Architecture: Kotlin Multiplatform (KMP) Ready

```
quizesque/
├── core/                    # Pure Kotlin (JVM + JS + Native)
│   ├── model/               # Question, Answer, Run, TopicStats, Seed logic
│   ├── engine/              # Scoring, Confidence Plays, Expedition generator
│   ├── persistence/         # Repository interfaces (Room / IndexedDB / SQLite)
│   └── content/             # Pack loader, schema validation, seed PRNG
├── android/                 # Compose UI, Room, Billing, Play Integrity
├── ios/                     # SwiftUI (later), SwiftData, StoreKit
├── web/                     # Compose Web / Kotlin/JS (later), IndexedDB, PWA
└── desktop/                 # Compose Desktop (optional, dev/debug)
```

**Rule:** All game logic, scoring, seeding, content parsing, and persistence contracts live in `core`. Platform modules only implement UI + platform adapters.

### 11.2 Rollout Phases

| Phase | Target | Timeline | Gate |
|-------|--------|----------|------|
| **v1.0** | Android (Phone) | Now → P2 | Play Store launch, crash-free > 99.5%, D1 > 25% |
| **v1.5** | Android Tablet / Foldable | +4 weeks | Adaptive layouts, stylus support |
| **v2.0** | iOS (iPhone) | +16–20 weeks | Core parity, StoreKit 2, TestFlight beta |
| **v2.5** | Web (PWA) | +8 weeks after iOS | Offline-first, shareable seeds via URL |
| **v3.0** | Desktop / TV | Opportunistic | Compose Multiplatform maturity |

### 11.3 Data & Sync Strategy

- **v1–v1.5:** Local-only (Room). Encrypted backup to Google Drive (opt-in, user-triggered).
- **v2.0+:** Optional cloud sync (Firebase / Supabase) for multi-device. Conflict resolution: last-write-wins per topic stats; merge run history by timestamp.
- **Never** require account for core loop. Account only for: cross-device sync, classroom teacher dashboard, purchase receipt validation.

### 11.4 Offline-First Guarantees

- Base app (30–100 questions) bundled in APK/AAB.
- Packs downloaded on-demand (compressed protobuf, ~50 KB / 100 questions).
- Daily Expedition seed generated client-side from date + app secret — no network call.
- Friend challenge seeds shared via deep link / QR / text — no server.

---

## 12. Launch Checklist

### 12.1 Pre-Launch (P0 → P1)

| Item | Owner | Done? | Notes |
|------|-------|-------|-------|
| Practice toggle + Retry-misses + Field Notes share | Dev | ☐ | 2-week sprint (Appendix C) |
| Question v2 schema + CI validation | Dev + Content | ☐ | Unblocks packs |
| Confidence Plays + Focus budget + calibration display | Dev | ☐ | Core mechanic |
| 30 base questions tagged + sourced + reviewed | Content | ☐ | §10 pipeline |
| Accessibility audit (TalkBack, contrast, timer-off) | QA | ☐ | Guardrail §6 |
| Crash reporting (Play Console + custom) | Dev | ☐ | No PII |
| Privacy policy + Play Data Safety form | Legal | ☐ | "Private by default" |
| Fake-door pack pages + waitlist capture | PM + Dev | ☐ | §9 validation |
| App icon, feature graphic, screenshots (Phone + Tablet) | Design | ☐ | Play Store reqs |
| Internal playtest (5+ people, 3 sessions each) | Team | ☐ | Weekly ritual §13 |

### 12.2 Launch Week

| Item | Owner | Done? |
|------|-------|-------|
| Staged rollout: 5% → 20% → 50% → 100% over 7 days | PM | ☐ |
| Monitor: crash rate, ANR, D1 retention, practice toggle adoption | PM + Dev | ☐ |
| Hotfix pipeline: build → internal test → Play Console in < 2h | Dev | ☐ |
| Support channel (email + in-app feedback) responsive < 24h | PM | ☐ |
| Social proof: 3–5 micro-influencer runs (study/quiz niches) | Marketing | ☐ |

### 12.3 Post-Launch (30 days)

| Metric | Target | Action if Missed |
|--------|--------|------------------|
| Crash-free sessions | > 99.5% | Pause rollout, fix top 3 crashes |
| D1 retention | > 25% | Simplify onboarding, add guided first run |
| Practice mode adoption | > 40% of runs | Make toggle more prominent |
| Field Notes share rate | > 5% of completions | Improve share card visual |
| Pack waitlist signups | > 200 | Adjust pack positioning / preview |

---

## 13. Team Rituals

| Ritual | Cadence | Duration | Participants | Output |
|--------|---------|----------|--------------|--------|
| **Weekly Playtest** | Every Friday | 60 min | Whole team (PM, Dev, Content, Design) | 3–5 UX fixes, 1 mechanic tweak |
| **Content Review** | Monthly | 90 min | Content lead + 1 dev + PM | Pack progress, dispute trends, schema changes |
| **Guardrail Audit** | Quarterly | 60 min | PM + Lead Dev + External advisor | Scorecard vs 6 guardrails, 1–2 policy updates |
| **Metrics Review** | Bi-weekly | 30 min | PM + Dev | Dashboard walk, decide A/B tests |
| **Retro** | Sprint end | 45 min | Dev team | Process improvements, tech debt tickets |

**Playtest protocol:** Each person plays 1 full run (Expedition or 10-Q) + 1 practice run. Notes in shared doc: "What felt smart?" "What felt unfair?" "Where did I want to quit?" No solutions — only observations.

---

## Appendix A: Seeded Challenge Specification

### A.1 Seed Format (v1)

```
seed = base64url(HMAC-SHA256(key, payload))
payload = version(1 byte) || type(1 byte) || date(4 bytes LE) || challengeId(16 bytes)
```

| Field | Size | Values |
|-------|------|--------|
| version | 1 byte | `0x01` |
| type | 1 byte | `0x01`=Daily Expedition, `0x02`=Friend Challenge, `0x03`=Custom |
| date | 4 bytes | Days since Unix epoch (little-endian) |
| challengeId | 16 bytes | Daily: all zeros. Friend: random 128-bit. Custom: creator-chosen. |

**Key derivation:** `key = HKDF-SHA256(app_secret, "seed-v1", context)` where `app_secret` is built into the binary (rotated per major version).

### A.2 Deterministic Expedition Generation

```kotlin
fun generateExpedition(seed: Seed, availableQuestions: List<Question>): Expedition {
  val rng = Xoroshiro128PlusPlus(seed.toLong())
  // 1. Pick 5 topics from available pool (weighted by question count)
  // 2. For each topic, pick 2 questions (1 easy/med, 1 med/hard)
  // 3. Build route: linear with 1 branch point (player chooses A/B at stop 3)
  // 4. Assign Focus budget = 2 per run
  // 5. Return ordered stops + branch metadata
}
```

**Reproducibility test:** Same seed + same question set version → identical expedition. CI runs this test on every question pack update.

### A.3 Friend Challenge Flow

1. Creator taps "Challenge Friend" → app generates `Seed(type=0x02, challengeId=random)`.
2. Share sheet: deep link `quizesque://challenge/<base64url_seed>` + prefilled message.
3. Recipient opens link → app validates seed version, checks pack availability (prompts download if missing).
4. Both players run identical expedition. Results compared locally (no server).
5. Rematch = same seed (Pro) or new seed (free, 1/day).

---

## Appendix B: Scoring Formula (Confidence Plays)

### B.1 Definitions

| Symbol | Meaning |
|--------|---------|
| `B` | Base points by difficulty: EASY=100, MEDIUM=200, HARD=350 |
| `C` | Confidence multiplier: UNSURE=1.0, LEANING=1.5, CERTAIN=2.0 |
| `F` | Focus spent this question: 0, 1 (clue), or 1 (shield) |
| `correct` | Boolean: player answer matches correctIndex |
| `penalty` | Points lost on wrong answer |

### B.2 Formula

```
if correct:
  score = B × C
  // Focus-as-clue: no bonus, but clue may have enabled correct answer
else:
  if F == SHIELD:
    penalty = B           // capped at base, ignores confidence
  else:
    penalty = B × C       // full confidence multiplier applies
  score = -penalty
```

**Focus budget:** 2 per 10-question run. Player chooses per question: **Clue** (reveal 1 distractor / context hint) or **Shield** (cap penalty). Cannot use both on same question.

### B.3 Calibration Score

```
For each confidence level L in {UNSURE, LEANING, CERTAIN}:
  calibration[L] = correct_count[L] / total_count[L]   // 0.0–1.0
Overall calibration = weighted avg by total_count[L]
```

Displayed on Result screen:
> **Calibration:** You said "Certain" and were right 73% of the time (11/15).  
> **Ideal:** Certain → ~90%, Leaning → ~65%, Unsure → ~35%.

### B.4 Example Scenarios

| Difficulty | Confidence | Focus | Correct? | Score | Note |
|------------|------------|-------|----------|-------|------|
| MEDIUM (200) | CERTAIN (2.0) | none | Yes | +400 | High risk, high reward |
| MEDIUM (200) | CERTAIN (2.0) | none | No | -400 | Teaches calibration |
| MEDIUM (200) | CERTAIN (2.0) | SHIELD | No | -200 | Shield saved 200 pts |
| HARD (350) | LEANING (1.5) | CLUE | Yes | +525 | Clue helped |
| EASY (100) | UNSURE (1.0) | none | Yes | +100 | Honest low confidence |

---

## Appendix C: P0 Sprint Plan (2 Weeks)

### Sprint Goal
Ship practice toggle, Retry-misses, Field Notes share, and difficulty ramp — all demoable, no new persistence.

### Week 1: Core Loop Fixes

| Day | Task | Owner | Demoable? |
|-----|------|-------|-----------|
| Mon | Add `practiceMode: Boolean` to `QuizConfig`; wire Home toggle → QuizScreen | Dev | ✅ |
| Tue | Remove hard-coded `TIME_PER_QUESTION`; use `config.timeLimitMs` (null = no timer) | Dev | ✅ |
| Wed | ResultScreen: add "Retry Misses" button → new run with only missed questions | Dev | ✅ |
| Thu | ResultScreen: keep `explanation` in `AnsweredQuestion`; render in recap list | Dev | ✅ |
| Fri | **Playtest** (whole team): 2 runs each (timer + no-timer), note friction | Team | — |

### Week 2: Field Notes + Polish

| Day | Task | Owner | Demoable? |
|-----|------|-------|-----------|
| Mon | Field Notes composable: Q + user answer + correct + explanation + source | Dev | ✅ |
| Tue | Share intent: "Copy as text" + "Share image" (Compose → Bitmap) | Dev | ✅ |
| Wed | Difficulty ramp: sort questions in `QuizViewModel` by `difficulty` tag (E→M→H) | Dev | ✅ |
| Thu | Accessibility pass: TalkBack labels, contrast, timer-off persists across runs | Dev + QA | ✅ |
| Fri | **Playtest + Sprint Demo** → stakeholder sign-off for P1 | Team | — |

### Definition of Done (per ticket)
- Code merged to `main`, CI green.
- Tested on API 24 + 34 (emulator + physical).
- No new warnings in `detekt` / `lint`.
- Playtest notes addressed or triaged to backlog.

### Stretch (if velocity allows)
- Persist `practiceMode` preference (DataStore).
- Add "Share run summary" (score + calibration + weak topics) to ResultScreen.
- Animated transition for Retry-misses (shared element).
