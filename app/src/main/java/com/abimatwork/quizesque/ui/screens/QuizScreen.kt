package com.abimatwork.quizesque.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abimatwork.quizesque.model.Question
import com.abimatwork.quizesque.ui.QuizViewModel
import com.abimatwork.quizesque.ui.components.QuizesqueMark
import com.abimatwork.quizesque.ui.components.StudioBootLoader
import kotlinx.coroutines.delay

private const val TIME_PER_QUESTION = 30
private const val BOOT_DURATION_MS = 2800L

private val Ink = Color(0xFF090B0D)
private val Panel = Color(0xFF14181B)
private val Gold = Color(0xFFF3BD55)
private val Muted = Color(0xFF92999C)
private val Mint = Color(0xFF9AD4C0)
private val Ember = Color(0xFFFF896E)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onQuizFinished: () -> Unit,
    onExit: () -> Unit
) {
    val question = viewModel.currentQuestion
    val index = viewModel.currentIndex
    val total = viewModel.totalQuestions

    // Studio boot overlay owns the screen while the arena "loads".
    var booting by remember { mutableStateOf(true) }
    var bootNonce by remember { mutableIntStateOf(0) }
    var timeLeft by remember(index) { mutableIntStateOf(TIME_PER_QUESTION) }

    LaunchedEffect(booting, index, viewModel.isLocked, bootNonce) {
        if (booting) return@LaunchedEffect
        while (timeLeft > 0 && !viewModel.isLocked) {
            delay(1000)
            timeLeft--
        }
        if (timeLeft == 0 && !viewModel.isLocked) viewModel.timeOut()
    }

    if (question == null) {
        LaunchedEffect(Unit) { onExit() }
        return
    }

    val reveal by animateFloatAsState(
        targetValue = if (booting) 0f else 1f,
        animationSpec = tween(520, easing = FastOutSlowInEasing),
        label = "reveal"
    )

    fun replayBoot() {
        if (booting) return
        timeLeft = TIME_PER_QUESTION
        bootNonce++
        booting = true
    }

    Box(Modifier.fillMaxSize().background(Ink)) {
        // Ambient background, matching the home screen's studio haze + dot grid
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF4D3516).copy(alpha = .22f), Color.Transparent),
                    radius = size.width * .8f
                ),
                radius = size.width * .8f,
                center = Offset(size.width * .1f, size.height * 0f)
            )
            val step = 34.dp.toPx()
            for (x in 0..(size.width / step).toInt()) for (y in 0..(size.height / step).toInt()) {
                drawCircle(
                    Color.White.copy(alpha = .02f),
                    radius = 1.dp.toPx(),
                    center = Offset(x * step, y * step)
                )
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .graphicsLayer { alpha = reveal; translationY = (1f - reveal) * 40f }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
        ) {
            Spacer(Modifier.height(14.dp))

            // ---- Top bar: quit, arena, round chip (long-press replays the loader) ----
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color.White.copy(alpha = .06f))
                        .combinedClickable(onClick = onExit, onLongClick = { onExit() }),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Quit", tint = Muted, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(11.dp))
                QuizesqueMark(modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        (viewModel.category?.title ?: "Quiz").uppercase(),
                        color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "HOLD ROUND CHIP TO REPLAY BOOT",
                        color = Muted, fontSize = 8.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.2.sp
                    )
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(Gold.copy(alpha = .12f))
                        .border(1.dp, Gold.copy(alpha = .3f), RoundedCornerShape(9.dp))
                        .combinedClickable(onClick = { }, onLongClick = { replayBoot() })
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "ROUND ${(index + 1).toString().padStart(2, '0')}/$total",
                        color = Gold, fontSize = 9.sp, fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            // ---- Timer dial + score ----
            Row(verticalAlignment = Alignment.CenterVertically) {
                TimerDial(timeLeft = timeLeft, total = TIME_PER_QUESTION, modifier = Modifier.size(56.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("SCORE", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text(
                        viewModel.score.toString().padStart(2, '0'),
                        color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black, letterSpacing = .5.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("CORRECT", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text(
                        "${viewModel.score * 100 / total.coerceAtLeast(1)}%",
                        color = Mint, fontSize = 26.sp, fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // ---- Question progress meter ----
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(total) { i ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    i < viewModel.answers.size -> {
                                        val last = viewModel.answers.getOrNull(viewModel.answers.size - 1)
                                        if (i == viewModel.answers.size - 1 && last != null) {
                                            if (last.isCorrect) Mint else Ember
                                        } else if (last != null) Mint else Gold
                                    }
                                    i == index -> Gold
                                    else -> Color.White.copy(alpha = .1f)
                                }
                            )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---- Question card ----
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(400)) + slideInVertically(tween(450, easing = FastOutSlowInEasing)) { it / 4 }
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.horizontalGradient(listOf(Panel, Color(0xFF111416))))
                        .border(1.dp, Color.White.copy(alpha = .075f), RoundedCornerShape(18.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "QUESTION ${(index + 1).toString().padStart(2, '0')}",
                                color = Gold, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp
                            )
                            if (viewModel.isRemoteSession) {
                                Spacer(Modifier.width(9.dp))
                                Text(
                                    "LIVE · SUPABASE",
                                    color = Mint, fontSize = 8.sp, fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(9.dp))
                        Text(
                            question.question,
                            color = Color.White, fontSize = 18.sp, lineHeight = 25.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ---- Options ----
            question.options.forEachIndexed { optionIndex, option ->
                val state = optionState(
                    optionIndex = optionIndex,
                    selected = viewModel.selectedOption,
                    correctIndex = question.correctIndex,
                    locked = viewModel.isLocked || viewModel.isChecking
                )
                OptionButton(
                    text = option,
                    optionLabel = ('A' + optionIndex).toString(),
                    state = state,
                    enabled = !viewModel.isLocked && !viewModel.isChecking,
                    onClick = { viewModel.selectOption(optionIndex) }
                )
                Spacer(Modifier.height(9.dp))
            }

            // ---- Feedback ----
            if (viewModel.isLocked) {
                FeedbackCard(
                    question = question,
                    selected = viewModel.selectedOption,
                    revealPending = viewModel.isRevealing,
                    modifier = Modifier.graphicsLayer {
                        alpha = reveal
                    }
                )
                Spacer(Modifier.height(14.dp))
                NextButton(
                    isLast = viewModel.isLastQuestion,
                    onClick = {
                        if (viewModel.nextQuestion()) onQuizFinished()
                    }
                )
            } else {
                Spacer(Modifier.height(6.dp))
                if (viewModel.isChecking) {
                    Text(
                        "CHECKING ANSWER…",
                        color = Gold, fontSize = 10.sp, fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp
                    )
                } else {
                    Text(
                        "Lock in before the dial runs dry.",
                        color = Muted, fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(26.dp))
        }

        // ---- My studio loading animation, over the game screen ----
        if (booting) {
            StudioBootLoader(
                arenaName = viewModel.category?.title ?: "Quiz",
                durationMs = BOOT_DURATION_MS,
                onFinished = { booting = false }
            )
        }
    }
}

private enum class OptionState { Default, Correct, Wrong, Disabled }

private fun optionState(
    optionIndex: Int,
    selected: Int?,
    correctIndex: Int,
    locked: Boolean
): OptionState {
    if (!locked) return OptionState.Default
    // Remote rounds whose answer check failed: stay neutral instead of guessing.
    if (correctIndex !in 0..3) return if (optionIndex == selected) OptionState.Default else OptionState.Disabled
    if (optionIndex == correctIndex) return OptionState.Correct
    if (optionIndex == selected) return OptionState.Wrong
    return OptionState.Disabled
}

@Composable
private fun TimerDial(timeLeft: Int, total: Int, modifier: Modifier = Modifier) {
    val fraction = (timeLeft.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    val color = when {
        timeLeft <= 10 -> Ember
        timeLeft <= 20 -> Gold
        else -> Mint
    }
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f
            drawCircle(
                color = Color.White.copy(alpha = .08f),
                radius = r - 3.dp.toPx(),
                style = Stroke(width = 3.dp.toPx())
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * fraction,
                useCenter = false,
                topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                size = Size(size.width - 6.dp.toPx(), size.height - 6.dp.toPx()),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            timeLeft.toString(),
            color = color, fontSize = 15.sp, fontWeight = FontWeight.Black
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OptionButton(
    text: String,
    optionLabel: String,
    state: OptionState,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val accent = when (state) {
        OptionState.Correct -> Mint
        OptionState.Wrong -> Ember
        OptionState.Disabled -> Muted
        OptionState.Default -> Gold
    }
    val bg = when (state) {
        OptionState.Correct -> Mint.copy(alpha = .10f)
        OptionState.Wrong -> Ember.copy(alpha = .10f)
        else -> Panel
    }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (pressed) .98f else 1f, tween(110), label = "opt-press"
    )
    Row(
        Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(15.dp))
            .background(bg)
            .border(
                1.dp,
                if (state == OptionState.Default) Color.White.copy(alpha = .08f) else accent.copy(alpha = .45f),
                RoundedCornerShape(15.dp)
            )
            .combinedClickable(
                enabled = enabled,
                onClick = {
                    pressed = true
                    onClick()
                },
                onLongClick = { pressed = true; onClick() }
            )
            .padding(horizontal = 14.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(accent.copy(alpha = .12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                optionLabel,
                color = accent, fontSize = 12.sp, fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.width(13.dp))
        Text(
            text,
            color = if (state == OptionState.Disabled) Muted else Color.White,
            fontSize = 14.sp,
            fontWeight = if (state == OptionState.Correct || state == OptionState.Wrong) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        if (state == OptionState.Correct) {
            Text("✓", color = Mint, fontSize = 16.sp, fontWeight = FontWeight.Black)
        } else if (state == OptionState.Wrong) {
            Text("✕", color = Ember, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun FeedbackCard(
    question: Question,
    selected: Int?,
    revealPending: Boolean,
    modifier: Modifier = Modifier
) {
    val revealed = question.isAnswerRevealed
    val correct = revealed && selected != null && selected == question.correctIndex
    val accent = if (correct) Mint else Ember
    val title = when {
        selected == null -> "TIME'S UP"
        !revealed -> "NOT VERIFIED"
        correct -> "CORRECT"
        else -> "NOT QUITE"
    }
    val body = when {
        selected == null && revealed -> "Correct answer: ${question.options[question.correctIndex]}. ${question.explanation}"
        selected == null && revealPending -> "Time's up — pulling the answer from Supabase…"
        selected == null -> "Time's up — and the answer could not be loaded, so it stays unverified."
        !revealed -> "Supabase did not confirm this answer, so the round counts as missed. Check your connection for the next one."
        correct -> question.explanation
        else -> "Correct answer: ${question.options[question.correctIndex]}. ${question.explanation}"
    }
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(accent.copy(alpha = .09f))
            .border(1.dp, accent.copy(alpha = .35f), RoundedCornerShape(15.dp))
            .padding(15.dp)
    ) {
        Column {
            Text(title, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(7.dp))
            Text(body, color = Color(0xFFD5DADE), fontSize = 13.sp, lineHeight = 20.sp)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NextButton(isLast: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFFF0B94E), Color(0xFFD89A2C))))
            .combinedClickable(onClick = onClick, onLongClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            if (isLast) "SEE RESULTS" else "NEXT ROUND",
            color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp
        )
    }
}
