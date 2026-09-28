package com.example.quizmaster.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.quizmaster.data.QuestionBank
import com.example.quizmaster.model.AnsweredQuestion
import com.example.quizmaster.model.Question
import com.example.quizmaster.model.QuizCategory

class QuizViewModel : ViewModel() {

    var category: QuizCategory? by mutableStateOf(null)
        private set

    var questions: List<Question> by mutableStateOf(emptyList())
        private set

    var currentIndex: Int by mutableIntStateOf(0)
        private set

    var score: Int by mutableIntStateOf(0)
        private set

    var selectedOption: Int? by mutableStateOf(null)
        private set

    var isLocked: Boolean by mutableStateOf(false)
        private set

    private val _answers = mutableStateListOf<AnsweredQuestion>()
    val answers: List<AnsweredQuestion> get() = _answers.toList()

    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val isLastQuestion: Boolean get() = currentIndex == questions.size - 1
    val totalQuestions: Int get() = questions.size

    fun startQuiz(category: QuizCategory) {
        this.category = category
        questions = QuestionBank.byCategory(category).shuffled()
        currentIndex = 0
        score = 0
        _answers.clear()
        resetQuestionState()
    }

    fun selectOption(index: Int) {
        if (isLocked || currentQuestion == null) return
        selectedOption = index
        isLocked = true
        val q = currentQuestion!!
        val correct = index == q.correctIndex
        if (correct) score++
        _answers.add(AnsweredQuestion(q, index, correct))
    }

    fun timeOut() {
        if (isLocked || currentQuestion == null) return
        isLocked = true
        selectedOption = null
        val q = currentQuestion!!
        _answers.add(AnsweredQuestion(q, null, false))
    }

    fun nextQuestion(): Boolean {
        // returns true if quiz finished after moving
        if (currentIndex < questions.size - 1) {
            currentIndex++
            resetQuestionState()
            return false
        }
        return true
    }

    fun restart() {
        category?.let { startQuiz(it) }
    }

    private fun resetQuestionState() {
        selectedOption = null
        isLocked = false
    }

    fun grade(): String = when {
        questions.isEmpty() -> "-"
        score == questions.size -> "Perfect! 🏆"
        score >= (questions.size * 0.8) -> "Excellent! 🌟"
        score >= (questions.size * 0.6) -> "Good Job! 👍"
        score >= (questions.size * 0.4) -> "Keep Trying! 💪"
        else -> "Better Luck Next Time! 📚"
    }
}
