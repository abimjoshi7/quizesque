package com.example.quizmaster.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizmaster.ui.QuizViewModel
import kotlinx.coroutines.delay

private const val TIME_PER_QUESTION = 30

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onQuizFinished: () -> Unit,
    onExit: () -> Unit
) {
    val question = viewModel.currentQuestion
    val index = viewModel.currentIndex
    val total = viewModel.totalQuestions

    var timeLeft by remember(index) { mutableIntStateOf(TIME_PER_QUESTION) }

    LaunchedEffect(index, viewModel.isLocked) {
        if (!viewModel.isLocked) {
            while (timeLeft > 0 && !viewModel.isLocked) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft == 0 && !viewModel.isLocked) {
                viewModel.timeOut()
            }
        }
    }

    if (question == null) {
        onExit()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(viewModel.category?.title ?: "Quiz", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {},
                actions = {
                    OutlinedButton(onClick = onExit, modifier = Modifier.padding(end = 8.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Quit")
                        Text(" Quit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Q ${index + 1} / $total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Timer, contentDescription = null, tint = timerColor(timeLeft))
                    Text(
                        text = " ${timeLeft}s",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = timerColor(timeLeft)
                    )
                }
                Text(
                    text = "Score: ${viewModel.score}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { (index + 1).toFloat() / total.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
            LinearProgressIndicator(
                progress = { timeLeft.toFloat() / TIME_PER_QUESTION.toFloat() },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = timerColor(timeLeft),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(20.dp)
                )
            }

            question.options.forEachIndexed { optionIndex, option ->
                OptionButton(
                    text = option,
                    optionLabel = ('A' + optionIndex).toString(),
                    state = optionState(
                        optionIndex = optionIndex,
                        selected = viewModel.selectedOption,
                        correctIndex = question.correctIndex,
                        locked = viewModel.isLocked
                    ),
                    enabled = !viewModel.isLocked,
                    onClick = { viewModel.selectOption(optionIndex) }
                )
            }

            if (viewModel.isLocked) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (viewModel.selectedOption == question.correctIndex)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Icon(
                            imageVector = if (viewModel.selectedOption == question.correctIndex)
                                Icons.Filled.CheckCircle else Icons.Filled.Close,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = if (viewModel.selectedOption == null)
                                "Time's up! Correct: ${question.options[question.correctIndex]}. ${question.explanation}"
                            else if (viewModel.selectedOption == question.correctIndex)
                                "Correct! ${question.explanation}"
                            else
                                "Wrong. Correct: ${question.options[question.correctIndex]}. ${question.explanation}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Button(
                    onClick = {
                        val finished = viewModel.nextQuestion()
                        if (finished) onQuizFinished()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (viewModel.isLastQuestion) "See Results" else "Next Question")
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select an answer before the timer runs out.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
    if (optionIndex == correctIndex) return OptionState.Correct
    if (optionIndex == selected) return OptionState.Wrong
    return OptionState.Disabled
}

@Composable
private fun OptionButton(
    text: String,
    optionLabel: String,
    state: OptionState,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val container = when (state) {
        OptionState.Correct -> MaterialTheme.colorScheme.primaryContainer
        OptionState.Wrong -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = container,
            disabledContainerColor = container,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = "$optionLabel. $text",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (state == OptionState.Correct || state == OptionState.Wrong)
                FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun timerColor(timeLeft: Int): Color {
    return when {
        timeLeft <= 10 -> MaterialTheme.colorScheme.error
        timeLeft <= 20 -> Color(0xFFE65100)
        else -> MaterialTheme.colorScheme.primary
    }
}
