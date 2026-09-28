package com.example.quizmaster.model

enum class QuizCategory(val id: String, val title: String, val description: String) {
    GK("gk", "General Knowledge", "World, science, history & more"),
    WORD("word", "Word / Vocabulary", "Meanings, synonyms & antonyms"),
    SPORTS("sports", "Sports", "Cricket, football, Olympics & more");

    companion object {
        fun fromId(id: String): QuizCategory =
            entries.first { it.id == id }
    }
}

data class Question(
    val id: Int,
    val category: QuizCategory,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
) {
    init {
        require(options.size == 4) { "Each question must have exactly 4 options" }
        require(correctIndex in 0..3) { "correctIndex must be 0..3" }
    }
}

data class AnsweredQuestion(
    val question: Question,
    val selectedIndex: Int?, // null = timed out / skipped
    val isCorrect: Boolean
)
