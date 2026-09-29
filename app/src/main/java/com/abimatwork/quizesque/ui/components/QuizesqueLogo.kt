package com.abimatwork.quizesque.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The QUIZesque game logo.
 *
 * Mark: a geometric "Q" built from the Attatva figure-eight pill-arc language —
 * an open ring with a 45-degree tail, drawn as a metallic triple pass with the
 * gold bindu at its centre, the same bindu that anchors the Attatva Systems mark.
 *
 * Wordmark: "QUIZ" in gold over "esque" in platinum, with the gold hairline
 * under the leading Q that mirrors the underline under the leading A of the
 * Attatva wordmark.
 *
 * Everything is vector Canvas / Text — no drawables, no assets, scales clean
 * from a 20dp header badge to a full-bleed splash lockup.
 */

private val GoldPale = Color(0xFFFFF6DC)
private val Gold = Color(0xFFE8B451)
private val GoldMid = Color(0xFFD79B33)
private val GoldDeep = Color(0xFF7E5A1B)
private val Platinum = Color(0xFFE9E3D3)

/** Degrees of the ring left open, centred on the tail at 45deg (lower-right). */
private const val TAIL_DEG = 45f
private const val RING_GAP_DEG = 46f

/**
 * The mark on its own.
 *
 * @param drawProgress 0..1 — stroke draws on from the tail around the ring.
 *                      Leave at 1f for the resting state.
 * @param sheen 0..1 — travelling light pass across the metal.
 */
@Composable
fun QuizesqueMark(
    modifier: Modifier = Modifier,
    drawProgress: Float = 1f,
    sheen: Float = 0f,
    showBindu: Boolean = true,
    ambient: Boolean = true
) {
    Canvas(modifier = modifier) {
        drawQuizesqueMark(
            center = center,
            radius = size.minDimension / 2f * 0.82f,
            drawProgress = drawProgress,
            sheen = sheen,
            showBindu = showBindu,
            ambient = ambient
        )
    }
}

/**
 * The mark as a plain draw call, so other Canvas work (the studio boot dial)
 * can compose the game logo straight into its own composition.
 */
fun DrawScope.drawQuizesqueMark(
    center: Offset,
    radius: Float,
    drawProgress: Float = 1f,
    sheen: Float = -1f,
    showBindu: Boolean = true,
    ambient: Boolean = true
) {
    run {
        val cx = center.x
        val cy = center.y
        val r = radius
        val w = r * 0.30f

        // ambient bed so the mark is lit, not floating
        if (ambient) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(GoldDeep.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = r * 1.35f
                ),
                radius = r * 1.35f,
                center = Offset(cx, cy)
            )
        }

        val start = TAIL_DEG + RING_GAP_DEG / 2f
        val span = 360f - RING_GAP_DEG
        val drawn = span * drawProgress.coerceIn(0f, 1f)

        // --- ring: deep base, mid gold, pale core ---
        if (drawn > 0.5f) {
            val trim = if (drawProgress < 1f) {
                PathEffect.dashPathEffect(floatArrayOf(drawn, span * 1.4f), 0f)
            } else {
                null
            }
            listOf(
                1.55f to GoldDeep.copy(alpha = 0.30f),
                1.0f to Gold,
                0.30f to GoldPale
            ).forEach { (mul, col) ->
                drawArc(
                    color = col,
                    startAngle = start,
                    sweepAngle = drawn,
                    useCenter = false,
                    topLeft = Offset(cx - r, cy - r),
                    size = Size(r * 2f, r * 2f),
                    style = Stroke(width = w * mul, cap = StrokeCap.Round, pathEffect = trim)
                )
            }
        }

        // --- tail: a bar leaving through the gap at 45deg ---
        if (drawProgress > 0.12f) {
            val grow = ((drawProgress - 0.12f) / 0.88f).coerceIn(0f, 1f)
            // starts inside the ring so it reads as one continuous Q, not a slash
            val inner = r * 0.58f
            val outer = r * 1.26f
            val a = polarDeg(cx, cy, inner, TAIL_DEG)
            val b = polarDeg(cx, cy, inner + (outer - inner) * grow, TAIL_DEG)
            listOf(
                1.64f to GoldDeep.copy(alpha = 0.30f),
                1.08f to Gold,
                0.32f to GoldPale
            ).forEach { (mul, col) ->
                drawLine(col, a, b, strokeWidth = w * mul, cap = StrokeCap.Round)
            }
        }

        // --- bindu: the Attatva jewel, holding the counter ---
        if (showBindu && drawProgress > 0.55f) {
            val pop = ((drawProgress - 0.55f) / 0.45f).coerceIn(0f, 1f)
            val br = r * 0.20f * pop
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(GoldPale.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = br * 2.6f
                ),
                radius = br * 2.6f,
                center = Offset(cx, cy)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(GoldPale, Gold, GoldDeep),
                    center = Offset(cx - br * 0.3f, cy - br * 0.34f),
                    radius = br * 1.5f
                ),
                radius = br,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = br * 0.24f,
                center = Offset(cx - br * 0.32f, cy - br * 0.36f)
            )
        }

        // --- sheen rides the metal only ---
        if (sheen in 0.01f..0.99f) {
            val sx = cx + (sheen - 0.5f) * r * 2.4f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.13f), Color.Transparent),
                    center = Offset(sx, cy),
                    radius = r * 0.55f
                ),
                radius = r * 0.55f,
                center = Offset(sx, cy)
            )
        }
    }
}

private fun polarDeg(cx: Float, cy: Float, r: Float, deg: Float): Offset {
    val rad = deg * 0.017453292f
    return Offset(cx + r * kotlin.math.cos(rad), cy + r * kotlin.math.sin(rad))
}

/**
 * "QUIZ" gold over "esque" platinum, hairline gold underline under the Q.
 */
@Composable
fun QuizesqueWordmark(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 22.sp,
    tracking: TextUnit = 1.sp,
    gold: Color = Gold,
    tail: Color = Platinum
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Q",
                style = TextStyle(brush = Brush.horizontalGradient(listOf(GoldPale, gold, GoldMid))),
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = tracking
            )
            Spacer(modifier = Modifier.height(fontSize.value.dp * 0.06f))
            Box(
                modifier = Modifier
                    .width(fontSize.value.dp * 0.52f)
                    .height((fontSize.value * 0.055f).dp.coerceAtLeast(1.dp))
                    .background(
                        Brush.horizontalGradient(listOf(gold, GoldPale, gold))
                    )
            )
        }
        Text(
            text = "UIZ",
            color = gold,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            letterSpacing = tracking
        )
        Text(
            text = "esque",
            color = tail,
            fontSize = fontSize,
            fontWeight = FontWeight.Light,
            letterSpacing = tracking
        )
    }
}

/**
 * Horizontal lockup — mark beside the wordmark. For the splash's presents beat
 * and any narrow bar where a stacked lockup would eat vertical space.
 */
@Composable
fun QuizesqueInlineLockup(
    modifier: Modifier = Modifier,
    markSize: Dp = 64.dp,
    fontSize: TextUnit = 26.sp,
    gap: Dp = 16.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuizesqueMark(modifier = Modifier.size(markSize))
        Spacer(modifier = Modifier.width(gap))
        QuizesqueWordmark(fontSize = fontSize)
    }
}

/**
 * Stacked lockup — mark over wordmark. The splash hero.
 */
@Composable
fun QuizesqueLockup(
    modifier: Modifier = Modifier,
    markSize: Dp = 132.dp,
    fontSize: TextUnit = 30.sp,
    gap: Dp = 22.dp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        QuizesqueMark(modifier = Modifier.size(markSize))
        Spacer(modifier = Modifier.height(gap))
        QuizesqueWordmark(fontSize = fontSize)
    }
}

/**
 * Horizontal lockup for app bars: mark in a gold-hairline badge beside the
 * wordmark. Replaces the placeholder "A" tile.
 */
@Composable
fun QuizesqueBadge(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF141A1D), Color(0xFF0A0D0F)))
            )
            .border(1.dp, Gold.copy(alpha = 0.28f), RoundedCornerShape(size * 0.28f)),
        contentAlignment = Alignment.Center
    ) {
        QuizesqueMark(
            modifier = Modifier
                .fillMaxSize()
                .padding(size * 0.16f)
        )
    }
}

/**
 * Splash hero: the mark draws itself, the wordmark tracks in, the sheen
 * crosses the metal once. Returns the lockup with all motion driven by [play].
 */
@Composable
fun QuizesqueSplashLockup(
    modifier: Modifier = Modifier,
    markSize: Dp = 168.dp,
    fontSize: TextUnit = 32.sp
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val draw by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(1500, delayMillis = 120, easing = FastOutSlowInEasing),
        label = "logo-draw"
    )
    val sheen by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(1100, delayMillis = 1500, easing = LinearEasing),
        label = "logo-sheen"
    )
    val type by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(900, delayMillis = 1150, easing = FastOutSlowInEasing),
        label = "logo-type"
    )
    val sheenAlpha by animateFloatAsState(
        targetValue = if (sheen < 0.999f) 1f else 0f,
        animationSpec = tween(200),
        label = "logo-sheen-alpha"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        QuizesqueMark(
            modifier = Modifier.size(markSize),
            drawProgress = draw,
            sheen = if (sheenAlpha > 0.5f) sheen else -1f
        )
        Spacer(modifier = Modifier.height(26.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer {
                alpha = type
                scaleX = 0.92f + 0.08f * type
                scaleY = 0.92f + 0.08f * type
            }
        ) {
            QuizesqueWordmark(fontSize = fontSize)
        }
    }
}
