package com.abimatwork.quizesque.model

enum class QuizCategory(val id: String, val title: String, val description: String) {
    GK("gk", "General Knowledge", "World, science, history & more"),
    WORD("word", "Word / Vocabulary", "Meanings, synonyms & antonyms"),
    RIDDLE("riddle", "Riddles", "Brainteasers, puzzles & wordplay");

    companion object {
        fun fromId(id: String): QuizCategory =
            entries.first { it.id == id }
    }
}

/** Where a question came from: the bundled offline bank or a Supabase project. */
sealed interface QuestionSource {
    /** Shipped inside the APK; grading happens on device. */
    data object Bundled : QuestionSource

    /** Fetched from `published_quiz_content`; grading happens through `check_quiz_answer`. */
    data class Remote(val questionId: String, val version: Int) : QuestionSource
}

data class Question(
    val id: Int,
    val category: QuizCategory,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val source: QuestionSource = QuestionSource.Bundled
) {
    init {
        require(options.size == 4) { "Each question must have exactly 4 options" }
        require(correctIndex in -1..3) {
            "correctIndex must be 0..3, or -1 while a remote answer check is still pending"
        }
    }

    /** False while a remote answer is still unknown (correctIndex == -1). */
    val isAnswerRevealed: Boolean get() = correctIndex in 0..3
}

data class AnsweredQuestion(
    val question: Question,
    val selectedIndex: Int?, // null = timed out / skipped
    val isCorrect: Boolean
)
