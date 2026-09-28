package com.example.quizmaster.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sin

private const val SPLASH_DURATION_MS = 3400L

private val Gold = Color(0xFFFFD54F)
private val GoldDeep = Color(0xFFB8860B)
private val GoldPale = Color(0xFFFFF3C4)
private val NightTop = Color(0xFF1A1440)
private val NightBottom = Color(0xFF0B0820)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        onFinished()
    }

    val infinite = rememberInfiniteTransition(label = "splash")
    val time by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    // Light sweep across the studio name
    val sweep by infinite.animateFloat(
        initialValue = -400f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing, delayMillis = 900),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )
    // Emblem breathing glow
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "pulse"
    )

    var taglineVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1500)
        taglineVisible = true
    }
    val taglineAlpha by animateFloatAsState(
        targetValue = if (taglineVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "tagline"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(NightTop, NightBottom))
            )
    ) {
        // Rising golden particles
        ParticleField(time = time, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing emblem
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(190.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Gold.copy(alpha = 0.35f), Color.Transparent),
                            center = center,
                            radius = size.minDimension / 2f
                        )
                    )
                    // Rotating dashed orbit ring
                    drawArc(
                        color = Gold.copy(alpha = 0.7f),
                        startAngle = time * 360f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 5f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .graphicsLayer {
                            scaleX = pulse
                            scaleY = pulse
                        }
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF3B2E7A), Color(0xFF171233))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    EmblemEntrance {
                        Icon(
                            imageVector = Icons.Filled.SportsEsports,
                            contentDescription = "Attatva Games",
                            tint = Gold,
                            modifier = Modifier.size(72.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Studio name with staggered letter reveal + travelling shine
            Box(modifier = Modifier.clipToBounds()) {
                Row(horizontalArrangement = Arrangement.Center) {
                    "ATTATVA".forEachIndexed { index, ch ->
                        StudioLetter(
                            char = ch.toString(),
                            delayMs = 350L + index * 110L,
                            fontSize = 52
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .offset(x = sweep.dp / 4f)
                        .width(110.dp)
                        .height(70.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
            Row(horizontalArrangement = Arrangement.Center) {
                "GAMES".forEachIndexed { index, ch ->
                    StudioLetter(
                        char = ch.toString(),
                        delayMs = 1100L + index * 100L,
                        fontSize = 34
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider + tagline
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, Gold, Color.Transparent)
                        ),
                        RoundedCornerShape(1.dp)
                    )
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "P R E S E N T S",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = taglineAlpha }
            )
            Text(
                text = "QuizMaster",
                color = GoldPale,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = taglineAlpha }
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Cinematic loading bar
            SplashLoadingBar()
        }
    }
}

@Composable
private fun StudioLetter(char: String, delayMs: Long, fontSize: Int) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs)
        visible = true
    }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.2f,
        animationSpec = spring(
            dampingRatio = 0.42f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "letter-scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(350),
        label = "letter-alpha"
    )
    val rise by animateFloatAsState(
        targetValue = if (visible) 0f else 56f,
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "letter-rise"
    )
    Text(
        text = char,
        color = Gold,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .padding(horizontal = 1.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                translationY = rise
            }
    )
}

@Composable
private fun EmblemEntrance(content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(150)
        visible = true
    }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessLow
        ),
        label = "emblem"
    )
    Box(
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun ParticleField(time: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        // Soft ambient orbs
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF6A5ACD).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(size.width * 0.15f, size.height * 0.2f),
                radius = size.minDimension * 0.45f
            ),
            radius = size.minDimension * 0.45f,
            center = Offset(size.width * 0.15f, size.height * 0.2f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(GoldDeep.copy(alpha = 0.28f), Color.Transparent),
                center = Offset(size.width * 0.9f, size.height * 0.75f),
                radius = size.minDimension * 0.5f
            ),
            radius = size.minDimension * 0.5f,
            center = Offset(size.width * 0.9f, size.height * 0.75f)
        )
        // Rising sparks
        repeat(30) { i ->
            val seed = (i * 0.6180339887f) % 1f
            val speed = 0.04f + (seed * 0.09f)
            val progress = (time * speed * 14f + seed) % 1f
            val y = size.height * (1f - progress)
            val x = size.width * ((seed * 7.31f) % 1f) +
                sin((time * 6.283f + seed * 12f)) * 36f
            val radius = 2f + (seed * 97f % 5f)
            val alpha = (0.1f + 0.55f * (1f - progress)) *
                (0.6f + 0.4f * sin(time * 12.56f + seed * 20f))
            drawCircle(
                color = if (i % 4 == 0) GoldPale else Gold,
                radius = radius,
                center = Offset(x, y),
                alpha = alpha.coerceIn(0f, 1f)
            )
        }
    }
}

@Composable
private fun SplashLoadingBar() {
    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        val steps = 60
        repeat(steps) {
            delay(SPLASH_DURATION_MS / steps)
            progress = (it + 1) / steps.toFloat()
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(listOf(GoldDeep, Gold, GoldPale))
                    )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Loading ${(progress * 100).toInt()}%",
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 12.sp
        )
    }
}
