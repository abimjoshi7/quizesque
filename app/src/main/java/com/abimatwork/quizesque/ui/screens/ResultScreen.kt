package com.abimatwork.quizesque.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abimatwork.quizesque.ui.QuizViewModel
import com.abimatwork.quizesque.ui.components.QuizesqueBadge
import com.abimatwork.quizesque.ui.components.QuizesqueWordmark

private val Ink = Color(0xFF090B0D)
private val Panel = Color(0xFF15191C)
private val Gold = Color(0xFFF3BD55)
private val Green = Color(0xFF8ED5AD)
private val Red = Color(0xFFFF806F)
private val Muted = Color(0xFF92999C)

@Composable
fun ResultScreen(viewModel: QuizViewModel, onPlayAgain: () -> Unit, onHome: () -> Unit) {
    val total = viewModel.totalQuestions
    val score = viewModel.score
    val percent = if (total > 0) score * 100 / total else 0
    LazyColumn(Modifier.fillMaxSize().background(Ink).statusBarsPadding().navigationBarsPadding().padding(horizontal = 22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuizesqueBadge(size = 36.dp)
                Spacer(Modifier.width(11.dp))
                Column {
                    Text("ATTATVA  /  GAMES", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                    QuizesqueWordmark(fontSize = 15.sp, tracking = 0.6.sp)
                }
            }
            Spacer(Modifier.height(25.dp))
            Text("ROUND COMPLETE", color = Gold, fontSize = 10.sp, letterSpacing = 2.5.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(9.dp))
            Text(viewModel.grade().uppercase(), color = Color.White, fontSize = 31.sp, lineHeight = 36.sp, fontWeight = FontWeight.Black, letterSpacing = (-.7).sp)
            Spacer(Modifier.height(19.dp))
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Panel).border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(18.dp)).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("${viewModel.category?.title?.uppercase() ?: "QUIZ"}  /  SCORE", color = Muted, fontSize = 9.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(7.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$score", color = Gold, fontSize = 52.sp, lineHeight = 55.sp, fontWeight = FontWeight.Black)
                        Text(" / $total", color = Muted, fontSize = 20.sp, modifier = Modifier.padding(bottom = 7.dp))
                    }
                    Text("$percent% accuracy", color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
                }
                Box(Modifier.size(88.dp).clip(RoundedCornerShape(22.dp)).background(Gold.copy(alpha = .11f)), contentAlignment = Alignment.Center) {
                    Text("$percent%", color = Gold, fontSize = 21.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Action("PLAY AGAIN", true, Modifier.weight(1f), onPlayAgain)
                Action("HOME", false, Modifier.weight(1f), onHome)
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("ANSWER REPLAY", color = Color.White, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(10.dp)); Text("${viewModel.answers.count { it.isCorrect }} / $total RIGHT", color = Muted, fontSize = 9.sp, letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(4.dp))
        }
        items(viewModel.answers) { answered ->
            val tone = if (answered.isCorrect) Green else if (answered.question.isAnswerRevealed) Red else Muted
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Panel).border(1.dp, Color.White.copy(alpha = .05f), RoundedCornerShape(14.dp)).padding(14.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(if (answered.isCorrect) Icons.Filled.Check else Icons.Filled.Close, null, tint = tone, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(9.dp))
                    Text(answered.question.question, Modifier.weight(1f), color = Color.White, fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(9.dp))
                val verified = answered.question.isAnswerRevealed
                Text("YOU  ·  ${answered.selectedIndex?.let { answered.question.options[it] } ?: "SKIPPED / TIME OUT"}", color = if (answered.isCorrect) Green else if (verified) Red else Muted, fontSize = 10.sp, lineHeight = 15.sp, letterSpacing = .3.sp)
                if (!answered.isCorrect) {
                    Text(
                        if (verified) "ANSWER  ·  ${answered.question.options[answered.question.correctIndex]}"
                        else "ANSWER  ·  NOT VERIFIED",
                        color = Color.White.copy(alpha = .85f), fontSize = 10.sp, lineHeight = 15.sp, letterSpacing = .3.sp
                    )
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun Action(label: String, primary: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val bg = if (primary) Gold else Panel
    val fg = if (primary) Ink else Color.White
    Row(modifier.clip(RoundedCornerShape(12.dp)).background(bg).border(if (primary) 0.dp else 1.dp, Color.White.copy(alpha = .12f), RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(vertical = 15.dp), horizontalArrangement = Arrangement.Center) {
        Text(label, color = fg, fontSize = 10.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
    }
}
