package com.abimatwork.quizesque.ui.screens

import android.app.Activity
import android.graphics.PathMeasure
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.abimatwork.quizesque.ui.components.QuizesqueInlineLockup
import kotlinx.coroutines.delay
import kotlin.math.sin

private const val SPLASH_DURATION_MS = 4400L

// ---- Palette sampled from attatva-systems-logo-final.png, extended metallic ramp ----
private val LogoBlack = Color(0xFF060606)
private val GoldHighlight = Color(0xFFFFF6D8)
private val ChampagneLight = Color(0xFFFFE9A8)
private val Champagne = Color(0xFFFFD166)
private val GoldMid = Color(0xFFE2A93B)
private val GoldDeep = Color(0xFF9A6B14)
private val GoldShadow = Color(0xFF5C3F0C)
private val Platinum = Color(0xFFF2EAD8)
private val SilverText = Color(0xFFE9E3D3)
private val SilverDim = Color(0xFFB9B2A0)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    SplashStatusBar()
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        onFinished()
    }

    var entered by remember { mutableStateOf(false) }
    var exiting by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50)
        entered = true
        delay(SPLASH_DURATION_MS - 450)
        exiting = true
    }
    val fadeIn by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "fadeIn"
    )
    val fadeOut by animateFloatAsState(
        targetValue = if (exiting) 0f else 1f,
        animationSpec = tween(450, easing = FastOutSlowInEasing),
        label = "fadeOut"
    )
    val contentAlpha = fadeIn * fadeOut

    // Cinematic timeline
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    // Mark draws on 0.15 -> 1.5s
    val markProgress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(1350, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "mark"
    )
    // Bindu ignition at ~1.05s
    var dotVisible by remember { mutableStateOf(false) }
    var ringVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1050)
        dotVisible = true
        ringVisible = true
    }
    val dotScale by animateFloatAsState(
        targetValue = if (dotVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "dot"
    )
    val ringP by animateFloatAsState(
        targetValue = if (ringVisible) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "ring"
    )
    var glowOn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1200)
        glowOn = true
    }
    val glow by animateFloatAsState(
        targetValue = if (glowOn) 1f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "glow"
    )
    // Single light pass across mark + wordmark, 1.9 -> 3.3s
    val sweep by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(1400, delayMillis = 1900, easing = LinearEasing),
        label = "sweep"
    )
    var tailVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(2500)
        tailVisible = true
    }
    val tailAlpha by animateFloatAsState(
        targetValue = if (tailVisible) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "tail"
    )
    val dividerProgress by animateFloatAsState(
        targetValue = if (tailVisible) 1f else 0f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "divider"
    )

    val goldBrush = remember {
        Brush.linearGradient(
            listOf(GoldHighlight, Champagne, GoldMid, GoldDeep),
            start = Offset(0f, 0f),
            end = Offset(900f, 600f)
        )
    }

    val bokehTime = rememberBokehTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LogoBlack)
    ) {
        // Warm studio haze, top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF3A2C10).copy(alpha = 0.5f),
                            Color.Transparent
                        ),
                        radius = 900f
                    )
                )
        )
        // Out-of-focus gold bokeh, very dim
        BokehField(time = bokehTime, modifier = Modifier.fillMaxSize())
        // Bottom scrim for depth
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, LogoBlack.copy(alpha = 0.55f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .graphicsLayer { alpha = contentAlpha },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AttatvaMark(
                progress = markProgress,
                dotScale = dotScale,
                glow = glow,
                ringP = ringP,
                sweep = sweep,
                modifier = Modifier
                    .width(260.dp)
                    .height(176.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Wordmark with travelling sheen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    val letters = listOf("Λ", "T", "T", "Λ", "T", "V", "Λ")
                    letters.forEachIndexed { index, ch ->
                        LogoLetter(
                            char = ch,
                            delayMs = 1500L + index * 110L,
                            fontSize = 46,
                            brush = goldBrush,
                            first = index == 0
                        )
                    }
                }
                if (sweep in 0.01f..0.99f) {
                    Box(
                        modifier = Modifier
                            .offset(x = ((sweep * 440f) - 220f).dp)
                            .width(64.dp)
                            .height(64.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.13f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                "SYSTEMS".forEachIndexed { index, ch ->
                    LogoSubLetter(
                        char = ch.toString(),
                        delayMs = 2100L + index * 70L
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hairline divider with centre diamond
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(190.dp * dividerProgress)
                        .height(1.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Champagne, Color.Transparent)
                            )
                        )
                )
                if (dividerProgress > 0.5f) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .graphicsLayer {
                                rotationZ = 45f
                                alpha = (dividerProgress - 0.5f) * 2f
                            }
                            .background(Champagne)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer { alpha = tailAlpha }
            ) {
                Text(
                    text = "P R E S E N T S",
                    color = SilverDim,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                // The game identity: QUIZesque mark + wordmark
                QuizesqueInlineLockup(
                    markSize = 58.dp,
                    fontSize = 25.sp,
                    gap = 15.dp
                )
            }

            Spacer(modifier = Modifier.height(42.dp))

            PremiumLoadingBar(
                modifier = Modifier.graphicsLayer { alpha = tailAlpha }
            )
        }
    }
}

/** Black status bar with light icons while the splash owns the screen. */
@Composable
private fun SplashStatusBar() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as Activity).window
        val controller = WindowCompat.getInsetsController(window, view)
        window.statusBarColor = android.graphics.Color.BLACK
        controller.isAppearanceLightStatusBars = false
        onDispose {
            // Theme mandates brand-black + light icons everywhere; re-assert (no restore
            // to the pre-splash color, which may predate the theme SideEffect).
            window.statusBarColor = android.graphics.Color.BLACK
            controller.isAppearanceLightStatusBars = false
        }
    }
}

/**
 * Vector replica of the Attatva Systems emblem, rendered as layered
 * metallic strokes: deep base, mid gold, pale core. The drawing head
 * carries a glow; the bindu ignites with a bloom + expanding ring.
 */
@Composable
private fun AttatvaMark(
    progress: Float,
    dotScale: Float,
    glow: Float,
    ringP: Float,
    sweep: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val strokeW = h * 0.062f
        val gap = w * 0.075f
        val leftMost = w * 0.075f
        val rightMost = w * 0.925f
        val topY = h * 0.07f
        val topInnerY = h * 0.455f
        val botInnerY = h * 0.545f
        val botY = h * 0.93f

        fun buildLoop(edgeOuterY: Float, edgeInnerY: Float, innerIsBottom: Boolean): Path {
            val r = kotlin.math.abs(edgeInnerY - edgeOuterY) / 2f
            val leftCx = leftMost + r
            val rightCx = rightMost - r
            val cy = (edgeOuterY + edgeInnerY) / 2f
            val halfPi = Math.PI.toFloat() / 2f
            val pts = mutableListOf<Offset>()
            fun lineToPts(x0: Float, y0: Float, x1: Float, y1: Float, n: Int, skipFirst: Boolean) {
                for (i in (if (skipFirst) 1 else 0) until n) {
                    val t = i / (n - 1f)
                    pts += Offset(x0 + (x1 - x0) * t, y0 + (y1 - y0) * t)
                }
            }
            fun arcPts(ccx: Float, n: Int, from: Float, delta: Float) {
                for (i in 1..n) {
                    val th = from + delta * i / n
                    pts += Offset(ccx + r * kotlin.math.cos(th), cy + r * kotlin.math.sin(th))
                }
            }
            if (innerIsBottom) {
                lineToPts(cx - gap, edgeInnerY, leftCx, edgeInnerY, 16, false)
                arcPts(leftCx, 40, halfPi, Math.PI.toFloat())           // bottom -> top via left
                lineToPts(leftCx, edgeOuterY, rightCx, edgeOuterY, 30, true)
                arcPts(rightCx, 40, -halfPi, Math.PI.toFloat())         // top -> bottom via right
                lineToPts(rightCx, edgeInnerY, cx + gap, edgeInnerY, 16, true)
            } else {
                lineToPts(cx - gap, edgeInnerY, leftCx, edgeInnerY, 16, false)
                arcPts(leftCx, 40, 3f * halfPi, -Math.PI.toFloat())     // top -> bottom via left
                lineToPts(leftCx, edgeOuterY, rightCx, edgeOuterY, 30, true)
                arcPts(rightCx, 40, halfPi, -Math.PI.toFloat())         // bottom -> top via right
                lineToPts(rightCx, edgeInnerY, cx + gap, edgeInnerY, 16, true)
            }
            val p = Path()
            pts.forEachIndexed { i, o ->
                if (i == 0) p.moveTo(o.x, o.y) else p.lineTo(o.x, o.y)
            }
            return p
        }

        val topLoop = buildLoop(topY, topInnerY, true)
        val botLoop = buildLoop(botY, botInnerY, false)

        fun lengthOf(path: Path): Float {
            val m = PathMeasure()
            m.setPath(path.asAndroidPath(), false)
            return m.length
        }

        fun headAt(path: Path, fraction: Float): Offset? {
            val m = PathMeasure()
            m.setPath(path.asAndroidPath(), false)
            val len = m.length
            if (len <= 0f) return null
            val pos = FloatArray(2)
            m.getPosTan((len * fraction).coerceIn(0f, len), pos, null)
            return Offset(pos[0], pos[1])
        }

        val topLen = lengthOf(topLoop)
        val botLen = lengthOf(botLoop)
        val eased = FastOutSlowInEasing.transform(progress.coerceIn(0f, 1f))

        // Warm halo bed
        if (glow > 0f || eased > 0f) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        Champagne.copy(alpha = 0.16f * glow + 0.07f * eased),
                        Color.Transparent
                    ),
                    center = center,
                    radius = w * 0.42f
                ),
                radius = w * 0.42f,
                center = center
            )
        }

        // Metallic pass: soft outer aura, deep base, mid gold, pale core
        val passes = listOf(
            Triple(1.9f, GoldMid.copy(alpha = 0.16f), null as Brush?),
            Triple(1.0f, GoldShadow, null),
            Triple(0.62f, GoldMid, null),
            Triple(0.30f, ChampagneLight, null)
        )
        listOf(topLoop to topLen, botLoop to botLen).forEach { (loop, len) ->
            if (len <= 0f || eased <= 0.001f) return@forEach
            val trim = PathEffect.dashPathEffect(floatArrayOf(len * eased, len * 1.2f), 0f)
            passes.forEach { (mul, color, _) ->
                drawPath(
                    path = loop,
                    color = color,
                    style = Stroke(width = strokeW * mul, cap = androidx.compose.ui.graphics.StrokeCap.Round, pathEffect = trim)
                )
            }
        }

        // Light carried on each drawing head
        if (eased in 0.02f..0.998f) {
            listOf(topLoop, botLoop).forEach { loop ->
                headAt(loop, eased)?.let { head ->
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White.copy(alpha = 0.85f), Color.Transparent),
                            center = head,
                            radius = strokeW * 1.7f
                        ),
                        radius = strokeW * 1.7f,
                        center = head
                    )
                }
            }
        }

        // Passing sheen stays on the metal, never drifts into the black
        if (sweep in 0.01f..0.99f && eased > 0.9f) {
            val sx = w * (0.02f + 0.96f * sweep)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(sx, h * 0.4f),
                    radius = w * 0.14f
                ),
                radius = w * 0.14f,
                center = Offset(sx, h * 0.4f)
            )
        }

        // ---- Bindu ignition: bloom + expanding ring + sparkle ----
        if (dotScale > 0.01f) {
            val dotR = h * 0.062f * dotScale
            if (ringP < 1f) {
                // bloom flash settling into aura
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(
                            GoldHighlight.copy(alpha = 0.5f * (1f - ringP)),
                            Color.Transparent
                        ),
                        center = center,
                        radius = dotR * (7f - 3f * ringP)
                    ),
                    radius = dotR * (7f - 3f * ringP),
                    center = center
                )
                // expanding hairline ring
                drawCircle(
                    color = Champagne.copy(alpha = 0.8f * (1f - ringP)),
                    radius = dotR * (1.2f + ringP * 10f),
                    center = center,
                    style = Stroke(width = 2.5f)
                )
                // four-point sparkle while igniting
                if (ringP < 0.6f) {
                    val sLen = dotR * 3.5f * (1f - ringP * 0.5f)
                    val sAlpha = (1f - ringP / 0.6f) * 0.8f
                    drawLine(
                        Color.White.copy(alpha = sAlpha),
                        Offset(center.x - sLen, center.y),
                        Offset(center.x + sLen, center.y),
                        strokeWidth = 2f
                    )
                    drawLine(
                        Color.White.copy(alpha = sAlpha),
                        Offset(center.x, center.y - sLen),
                        Offset(center.x, center.y + sLen),
                        strokeWidth = 2f
                    )
                }
            }
            // resting aura
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(GoldMid.copy(alpha = 0.35f * glow), Color.Transparent),
                    center = center,
                    radius = dotR * 4f
                ),
                radius = dotR * 4f,
                center = center
            )
            // jewel sphere
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFF8DC), ChampagneLight, Champagne, GoldDeep),
                    center = Offset(center.x - dotR * 0.25f, center.y - dotR * 0.3f),
                    radius = dotR * 1.6f
                ),
                radius = dotR,
                center = center
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = dotR * 0.22f,
                center = Offset(center.x - dotR * 0.28f, center.y - dotR * 0.32f)
            )
        }
    }
}

@Composable
private fun LogoLetter(
    char: String,
    delayMs: Long,
    fontSize: Int,
    brush: Brush,
    first: Boolean
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs)
        visible = true
    }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "logo-letter"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "logo-alpha"
    )
    val rise by animateFloatAsState(
        targetValue = if (visible) 0f else 26f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
        label = "logo-rise"
    )
    // Tracking settles from wide to refined — signature premium reveal
    val tracking by animateFloatAsState(
        targetValue = if (visible) 2f else 12f,
        animationSpec = tween(750, easing = FastOutSlowInEasing),
        label = "logo-track"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = char,
            style = TextStyle(brush = brush),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = tracking.sp,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                    translationY = rise
                }
        )
        if (first) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .width(if (visible) 30.dp else 0.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(GoldMid)
                    .graphicsLayer { this.alpha = alpha }
            )
        }
    }
}

@Composable
private fun LogoSubLetter(char: String, delayMs: Long) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs)
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "sub-alpha"
    )
    val tracking by animateFloatAsState(
        targetValue = if (visible) 8f else 16f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "sub-track"
    )
    Text(
        text = char,
        color = SilverText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = tracking.sp,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .graphicsLayer { this.alpha = alpha }
    )
}

@Composable
private fun rememberBokehTime(): Float {
    val infinite = rememberInfiniteTransition(label = "bokeh")
    val t by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20_000, easing = LinearEasing)
        ),
        label = "bokehTime"
    )
    return t
}

/** Dim out-of-focus gold bokeh — slow, sparse, peripheral. */
@Composable
private fun BokehField(time: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        repeat(12) { i ->
            val seed = ((i * 0.6180339887f) % 1f)
            val speed = 0.03f + seed * 0.05f
            val progress = (time * speed * 20f + seed) % 1f
            val y = size.height * (1f - progress)
            val edgeBias = if (i % 2 == 0) seed * 0.28f else 0.72f + seed * 0.28f
            val x = size.width * edgeBias +
                sin((time * 6.283f + seed * 12f)) * 18f
            val radius = 10f + (seed * 53f % 22f)
            val alpha = (0.05f + 0.07f * (1f - progress)).coerceIn(0f, 0.12f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(GoldMid.copy(alpha = alpha), Color.Transparent),
                    center = Offset(x, y),
                    radius = radius
                ),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun PremiumLoadingBar(modifier: Modifier = Modifier) {
    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        val steps = 76
        repeat(steps) {
            delay(SPLASH_DURATION_MS / steps)
            progress = (it + 1) / steps.toFloat()
        }
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(170.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Color.White.copy(alpha = 0.10f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(
                        Brush.horizontalGradient(listOf(GoldDeep, Champagne, GoldHighlight))
                    )
            )
            // travelling head glow
            Box(
                modifier = Modifier
                    .offset(x = (170.dp * progress) - 3.dp)
                    .size(6.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Color.White.copy(alpha = 0.9f), Color.Transparent),
                            radius = 18f
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "PREPARING YOUR EXPERIENCE",
            color = SilverDim.copy(alpha = 0.8f),
            fontSize = 9.sp,
            letterSpacing = 3.sp
        )
    }
}
