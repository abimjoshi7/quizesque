package com.abimatwork.quizesque.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abimatwork.quizesque.model.QuizCategory
import com.abimatwork.quizesque.ui.components.QuizesqueBadge
import com.abimatwork.quizesque.ui.components.QuizesqueWordmark
import kotlinx.coroutines.delay

private val Ink = Color(0xFF090B0D)
private val Panel = Color(0xFF14181B)
private val Gold = Color(0xFFF3BD55)
private val Muted = Color(0xFF92999C)

@Composable
fun HomeScreen(onCategoryClick: (QuizCategory) -> Unit, onEnter: () -> Unit = {}) {
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        onEnter()
        delay(70)
        entered = true
    }
    Box(Modifier.fillMaxSize().background(Ink)) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Brush.radialGradient(listOf(Color(0xFF4D3516).copy(alpha = .28f), Color.Transparent), radius = size.width * .8f), radius = size.width * .8f, center = androidx.compose.ui.geometry.Offset(size.width * .9f, size.height * .05f))
            val step = 34.dp.toPx()
            for (x in 0..(size.width / step).toInt()) for (y in 0..(size.height / step).toInt()) {
                val px = x * step; val py = y * step
                drawCircle(Color.White.copy(alpha = .025f), radius = 1.dp.toPx(), center = androidx.compose.ui.geometry.Offset(px, py))
            }
        }
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp),
        ) {
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuizesqueBadge(size = 36.dp)
                Spacer(Modifier.width(11.dp))
                Column {
                    Text("ATTATVA  /  GAMES", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                    QuizesqueWordmark(fontSize = 15.sp, tracking = 0.6.sp)
                }
                Spacer(Modifier.weight(1f))
                Text("01 — 03", color = Gold, fontSize = 11.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(46.dp))
            AnimatedVisibility(visible = entered, enter = fadeIn(tween(500)) + slideInVertically(tween(650, easing = FastOutSlowInEasing)) { it / 5 }) {
                Column {
                    Text("THE QUICK", color = Color.White, fontSize = 39.sp, lineHeight = 42.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.2).sp)
                    Text("THINKING GAME.", color = Gold, fontSize = 35.sp, lineHeight = 40.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.4).sp)
                    Spacer(Modifier.height(15.dp))
                    Text("Three arenas. Ten rounds each.\nHow far can your knowledge take you?", color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
                }
            }
            Spacer(Modifier.height(30.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("PICK YOUR ARENA", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.width(12.dp)); Box(Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = .13f)))
            }
            Spacer(Modifier.height(14.dp))
            val cats = listOf(
                Triple(QuizCategory.GK, Icons.Filled.Lightbulb, "01"),
                Triple(QuizCategory.WORD, Icons.Filled.Abc, "02"),
                Triple(QuizCategory.RIDDLE, Icons.Filled.Extension, "03")
            )
            cats.forEachIndexed { i, (category, icon, number) ->
                AnimatedVisibility(visible = entered, enter = fadeIn(tween(420, delayMillis = 100 + i * 110)) + slideInVertically(tween(450, delayMillis = 100 + i * 110)) { it / 3 }) {
                    ArenaCard(category, icon, number, i, onClick = { onCategoryClick(category) })
                }
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("10 QUESTIONS", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
                Text("30 SEC / ROUND", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
            }
        }
    }
}

@Composable
private fun ArenaCard(category: QuizCategory, icon: ImageVector, number: String, index: Int, onClick: () -> Unit) {
    val accent = when (index) { 1 -> Color(0xFF9AD4C0); 2 -> Color(0xFFFF896E); else -> Gold }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) .975f else 1f, tween(120), label = "arena-press")
    Row(
        Modifier.fillMaxWidth().scale(scale).clip(RoundedCornerShape(17.dp))
            .background(Brush.horizontalGradient(listOf(Panel, Color(0xFF111416))))
            .border(1.dp, Color.White.copy(alpha = .075f), RoundedCornerShape(17.dp))
            .clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(accent.copy(alpha = .12f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(25.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(category.title.uppercase(), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = .35.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(3.dp))
            Text(category.description, color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(number, color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(5.dp))
            Text("↗", color = accent, fontSize = 20.sp, lineHeight = 20.sp)
        }
    }
}
