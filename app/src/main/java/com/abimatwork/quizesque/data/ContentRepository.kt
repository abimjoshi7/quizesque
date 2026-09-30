package com.abimatwork.quizesque.data

import android.util.Log
import com.abimatwork.quizesque.model.Question
import com.abimatwork.quizesque.model.QuestionSource
import com.abimatwork.quizesque.model.QuizCategory
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap

/** Correct index + explanation returned by an answer RPC. */
data class AnswerCheck(val correctIndex: Int, val explanation: String)

/** Row shape of `published_quiz_content` (the answer index is deliberately not exposed). */
@Serializable
internal data class ContentRow(
    @SerialName("question_id") val questionId: String,
    val version: Int,
    val prompt: String,
    val options: List<String>,
    val explanation: String
)

@Serializable
internal data class CheckAnswerParams(
    @SerialName("p_question_id") val questionId: String,
    @SerialName("p_version") val version: Int,
    @SerialName("p_selected_index") val selectedIndex: Int
)

@Serializable
internal data class RevealAnswerParams(
    @SerialName("p_question_id") val questionId: String,
    @SerialName("p_version") val version: Int
)

/**
 * Shared by `check_quiz_answer` (is_correct present) and `reveal_quiz_answer`
 * (is_correct absent), so [isCorrect] is optional.
 */
@Serializable
internal data class AnswerRow(
    @SerialName("is_correct") val isCorrect: Boolean? = null,
    @SerialName("correct_index") val correctIndex: Int,
    val explanation: String
)

/**
 * Reads published quiz content from Supabase and grades remote answers through
 * database RPCs. Everything is best-effort: when the project is unconfigured,
 * unreachable or empty, callers fall back to the bundled [QuestionBank].
 *
 * Remote questions never carry an answer index — only `check_quiz_answer` /
 * `reveal_quiz_answer` hand one back, and only after the player has committed
 * to a round (or run out of time).
 */
object ContentRepository {

    private const val TAG = "ContentRepository"
    private const val VIEW = "published_quiz_content"
    private const val LOCALE = "en-IN"

    private const val FETCH_TIMEOUT_MS = 8_000L
    private const val RPC_TIMEOUT_MS = 6_000L

    /** Home screen promises "ten rounds each", so a run never exceeds this. */
    private const val MAX_PER_RUN = 10
    /** Rows pulled per category; more than [MAX_PER_RUN] distinct questions is wasted work. */
    private const val MAX_ROWS = 30

    private const val UNANSWERED = -1

    private val cache = ConcurrentHashMap<String, List<Question>>()

    val isConfigured: Boolean get() = SupabaseProvider.isConfigured

    /** Already-fetched content for this category, shuffled for replay. */
    fun cached(category: QuizCategory): List<Question>? =
        cache[category.id]?.shuffled()

    /**
     * Fetch published questions for a category.
     * Returns null when there is nothing usable yet (unconfigured, offline,
     * timeout, or no published content) so callers can fall back offline.
     */
    suspend fun load(category: QuizCategory): List<Question>? {
        cache[category.id]?.let { return it.shuffled() }
        val client = SupabaseProvider.client ?: return null

        val rows = try {
            withTimeoutOrNull(FETCH_TIMEOUT_MS) {
                client.postgrest.from(VIEW).select {
                    filter {
                        eq("category_id", category.id)
                        eq("locale", LOCALE)
                    }
                    order("pack_position", Order.ASCENDING)
                    limit(MAX_ROWS.toLong())
                }.decodeList<ContentRow>()
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            Log.w(TAG, "Content load failed for ${category.id}: ${error.summary()}")
            null
        } ?: return null

        val questions = rows.asSequence()
            .distinctBy { it.questionId }
            .filter { it.questionId.isNotBlank() && it.options.size == 4 }
            .take(MAX_PER_RUN)
            .mapIndexed { position, row ->
                Question(
                    id = position,
                    category = category,
                    question = row.prompt,
                    options = row.options,
                    correctIndex = UNANSWERED,
                    explanation = row.explanation,
                    source = QuestionSource.Remote(row.questionId, row.version)
                )
            }
            .toList()

        if (questions.isEmpty()) {
            Log.i(TAG, "No published content for ${category.id} yet")
            return null
        }
        cache[category.id] = questions
        return questions.shuffled()
    }

    /** Ask the database whether [selectedIndex] was right. Null when it could not be checked. */
    suspend fun checkAnswer(
        source: QuestionSource.Remote,
        selectedIndex: Int
    ): AnswerCheck? = answerViaRpc(
        function = "check_quiz_answer",
        params = CheckAnswerParams(source.questionId, source.version, selectedIndex)
    )

    /** Fetch the correct answer after a timed-out remote round. Null when offline. */
    suspend fun revealAnswer(source: QuestionSource.Remote): AnswerCheck? = answerViaRpc(
        function = "reveal_quiz_answer",
        params = RevealAnswerParams(source.questionId, source.version)
    )

    private suspend inline fun <reified T : Any> answerViaRpc(
        function: String,
        params: T
    ): AnswerCheck? {
        val client = SupabaseProvider.client ?: return null
        val row = try {
            withTimeoutOrNull(RPC_TIMEOUT_MS) {
                client.postgrest.rpc(function, params).decodeList<AnswerRow>().firstOrNull()
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            Log.w(TAG, "$function failed: ${error.summary()}")
            null
        } ?: return null

        return AnswerCheck(row.correctIndex, row.explanation)
    }

    /**
     * Supabase REST errors carry the request URL and headers; keep the first line
     * only so logcat stays readable (and does not echo request headers).
     */
    private fun Throwable.summary(): String =
        message?.lineSequence()?.firstOrNull() ?: (this::class.simpleName ?: "error")
}
