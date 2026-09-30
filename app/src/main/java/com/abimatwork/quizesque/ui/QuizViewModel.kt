package com.abimatwork.quizesque.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abimatwork.quizesque.data.AnswerCheck
import com.abimatwork.quizesque.data.ContentRepository
import com.abimatwork.quizesque.data.QuestionBank
import com.abimatwork.quizesque.model.AnsweredQuestion
import com.abimatwork.quizesque.model.Question
import com.abimatwork.quizesque.model.QuestionSource
import com.abimatwork.quizesque.model.QuizCategory
import kotlinx.coroutines.launch

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

    /** True while a remote answer check is in flight; the options stay un-tappable. */
    var isChecking: Boolean by mutableStateOf(false)
        private set

    /** True while a timed-out remote round is fetching its answer. */
    var isRevealing: Boolean by mutableStateOf(false)
        private set

    /** True when this run is served by Supabase instead of the bundled bank. */
    val isRemoteSession: Boolean
        get() = questions.isNotEmpty() && questions.all { it.source is QuestionSource.Remote }

    private val _answers = mutableStateListOf<AnsweredQuestion>()
    val answers: List<AnsweredQuestion> get() = _answers.toList()

    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val isLastQuestion: Boolean get() = currentIndex == questions.size - 1
    val totalQuestions: Int get() = questions.size

    /** Bumped per run so late network replies from an abandoned run are dropped. */
    private var session = 0

    /** Warm the content cache from the home screen so the boot loader can mask the fetch. */
    fun prefetch() {
        if (!ContentRepository.isConfigured) return
        viewModelScope.launch {
            QuizCategory.entries.forEach { ContentRepository.prepare(it) }
        }
    }

    fun startQuiz(category: QuizCategory) {
        session++
        this.category = category
        currentIndex = 0
        score = 0
        _answers.clear()
        resetQuestionState()

        // Bundled questions show immediately; remote content replaces them only while
        // round one is still unanswered, so a run is never graded by two systems.
        val cachedRemote = ContentRepository.cached(category)
        questions = cachedRemote ?: QuestionBank.byCategory(category).shuffled()

        if (cachedRemote == null) {
            val scopeSession = session
            viewModelScope.launch {
                val remote = ContentRepository.load(category)
                if (remote != null &&
                    session == scopeSession &&
                    this@QuizViewModel.category == category &&
                    currentIndex == 0 &&
                    _answers.isEmpty()
                ) {
                    questions = remote
                }
            }
        }
    }

    fun selectOption(index: Int) {
        val question = currentQuestion ?: return
        if (isLocked || isChecking) return

        selectedOption = index
        when (val source = question.source) {
            QuestionSource.Bundled -> {
                isLocked = true
                val correct = index == question.correctIndex
                if (correct) score++
                _answers.add(AnsweredQuestion(question, index, correct))
            }
            is QuestionSource.Remote -> {
                isChecking = true
                val scopeSession = session
                viewModelScope.launch {
                    val check = ContentRepository.checkAnswer(source, index)
                    isChecking = false
                    if (session != scopeSession) return@launch
                    isLocked = true
                    if (check == null) {
                        // Unverified: recorded as not correct, and the round is not scored.
                        _answers.add(AnsweredQuestion(question, index, false))
                    } else {
                        val resolved = resolve(question, check)
                        val correct = check.correctIndex == index
                        if (correct) score++
                        _answers.add(AnsweredQuestion(resolved, index, correct))
                    }
                }
            }
        }
    }

    fun timeOut() {
        val question = currentQuestion ?: return
        if (isLocked || isChecking) return
        selectedOption = null
        isLocked = true
        _answers.add(AnsweredQuestion(question, null, false))

        // Bundled questions already know the answer; remote ones have to ask for it.
        val source = question.source as? QuestionSource.Remote ?: return
        val scopeSession = session
        isRevealing = true
        viewModelScope.launch {
            val revealed = ContentRepository.revealAnswer(source)
            if (session == scopeSession) isRevealing = false
            if (revealed == null || session != scopeSession) return@launch
            val resolved = resolve(question, revealed)
            val position = _answers.indexOfLast { it.question === question }
            if (position >= 0) {
                _answers[position] = _answers[position].copy(question = resolved)
            }
        }
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
        isChecking = false
        isRevealing = false
    }

    /** Fills in the answer index a remote question was fetched without. */
    private fun resolve(question: Question, check: AnswerCheck): Question {
        val resolved = question.copy(
            correctIndex = check.correctIndex,
            explanation = check.explanation.ifBlank { question.explanation }
        )
        val position = questions.indexOfFirst { it === question }
        if (position >= 0) questions = questions.toMutableList().also { it[position] = resolved }
        return resolved
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
