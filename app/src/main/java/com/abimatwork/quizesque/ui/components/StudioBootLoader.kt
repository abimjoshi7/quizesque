package com.abimatwork.quizesque.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * "STUDIO BOOT" — Attatva Games arena loader.
 *
 * My own take on the studio loading animation, deliberately built from a
 * different visual grammar than the SplashScreen emblem sequence:
 *   instrument dial (ticks + progress sweep + counter-rotating arcs)
 *   + ping waves + orbiting satellites + hex core
 *   + monospace telemetry readout with a typed arena name.
 *
 * Pure Canvas + Compose graphics, no assets, no XML.
 */

private val Void = Color(0xFF06080A)
private val GridLine = Color(0xFF1A2126)
private val Gold = Color(0xFFF3BD55)
private val GoldPale = Color(0xFFFFF0C8)
private val GoldDeep = Color(0xFF7E5A1B)
private val Signal = Color(0xFF7FE7D8)
private val Muted = Color(0xFF7E868A)

private val BOOT_STEPS = listOf(
    "MOUNTING ARENA BUNDLE",
    "SYNCING QUESTION BANK",
    "WARMING UP TIMER CORE",
    "CALIBRATING RING LIGHT",
    "ARENA READY"
)

private const val TAU = 6.2831855f
private const val DEG = 0.017453292f

private fun polar(cx: Float, cy: Float, r: Float, deg: Float) =
    Offset(cx + r * cos(deg * DEG), cy + r * sin(deg * DEG))

/**
 * Full-screen boot overlay. Self-terminating: fades itself out after
 * [durationMs] and only then calls [onFinished], so the parent can keep the
 * overlay mounted for the whole exit animation.
 */
@Composable
fun StudioBootLoader(
    arenaName: String,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    durationMs: Long = 2800L,
    signature: String = "DESIGNED & BUILT BY OPENCODE"
) {
    var done by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(durationMs); done = true }
    LaunchedEffect(done) {
        if (done) {
            delay(460L)
            onFinished()
        }
    }

    val exit by animateFloatAsState(
        targetValue = if (done) 0f else 1f,
        animationSpec = tween(440, easing = FastOutSlowInEasing),
        label = "boot-exit"
    )
    // Explicit 0 -> 1 driver: the meter owns its own clock rather than easing
    // toward a constant target.
    val progress = remember { Animatable(0f) }
    LaunchedEffect(durationMs) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = durationMs.toInt().coerceAtLeast(1),
                easing = CubicBezierEasing(0.45f, 0.05f, 0.55f, 0.95f)
            )
        )
    }
    val progressValue = progress.value

    val loop = rememberInfiniteTransition(label = "boot-loop")
    val spin by loop.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(7200, easing = LinearEasing)),
        label = "spin"
    )
    val orbit by loop.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3600, easing = LinearEasing)),
        label = "orbit"
    )
    val ping by loop.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "ping"
    )
    val pulse by loop.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val cursor by loop.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing)),
        label = "cursor"
    )

    val upper = arenaName.uppercase()
    val typed = upper.take((upper.length * progressValue).toInt().coerceIn(0, upper.length))
    val step = BOOT_STEPS[(progressValue * BOOT_STEPS.size).toInt().coerceIn(0, BOOT_STEPS.size - 1)]
    val percent = (progressValue * 100).toInt().coerceIn(0, 100)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Void)
            .graphicsLayer { alpha = exit }
    ) {
        BackdropHud(scan = ping, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BootDial(
                progress = progressValue,
                spin = spin,
                orbit = orbit,
                ping = ping,
                pulse = pulse,
                modifier = Modifier.size(232.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Typed arena name with blinking block cursor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = typed,
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.4.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(3.dp))
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(18.dp)
                        .background(
                            if (cursor > 0.5f) Gold else Color.Transparent
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = step,
                color = Signal,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.4.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            SegmentedBar(progress = progressValue, modifier = Modifier.fillMaxWidth().height(6.dp))

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ATTATVA GAMES  /  STUDIO",
                    color = Muted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.4.sp
                )
                Text(
                    text = percent.toString().padStart(3, '0') + "%",
                    color = Gold,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            StudioSignature(signature)
        }
    }
}

/** Instrument dial: ticks, sweep, counter-rotating arcs, satellites, hex core. */
@Composable
private fun BootDial(
    progress: Float,
    spin: Float,
    orbit: Float,
    ping: Float,
    pulse: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension / 2f

        // Warm bed so the dial sits in light, not in a void
        drawCircle(
            brush = Brush.radialGradient(
                listOf(GoldDeep.copy(alpha = 0.20f), Color.Transparent),
                center = Offset(cx, cy),
                radius = r * 1.05f
            ),
            radius = r * 1.05f,
            center = Offset(cx, cy)
        )

        // --- tick bezel (60 ticks, every 5th long) ---
        for (i in 0 until 60) {
            val deg = i * 6f - 90f
            val major = i % 5 == 0
            val len = if (major) r * 0.075f else r * 0.038f
            val a = if (major) 0.40f else 0.16f
            val col = if (major) Gold else GridLine
            drawLine(
                color = col.copy(alpha = a),
                start = polar(cx, cy, r * 0.90f - len, deg),
                end = polar(cx, cy, r * 0.90f, deg),
                strokeWidth = if (major) 1.6f else 1f,
                cap = StrokeCap.Round
            )
        }

        // --- track + progress sweep (metallic triple pass) ---
        drawCircle(
            color = Color.White.copy(alpha = 0.055f),
            radius = r * 0.78f,
            center = Offset(cx, cy),
            style = Stroke(width = r * 0.035f)
        )
        val sweepDeg = 360f * progress
        val passes = listOf(
            Triple(1.9f, GoldDeep.copy(alpha = 0.22f), r * 0.035f * 1.9f),
            Triple(1.0f, Gold, r * 0.035f),
            Triple(0.34f, GoldPale, r * 0.035f * 0.34f)
        )
        passes.forEach { (mul, col, width) ->
            if (progress <= 0.001f) return@forEach
            drawArc(
                color = col,
                startAngle = -90f,
                sweepAngle = sweepDeg,
                useCenter = false,
                topLeft = Offset(cx - r * 0.78f, cy - r * 0.78f),
                size = androidx.compose.ui.geometry.Size(r * 1.56f, r * 1.56f),
                style = Stroke(width = width, cap = StrokeCap.Round)
            )
        }

        // light carried on the sweep head
        if (progress in 0.004f..0.998f) {
            val head = polar(cx, cy, r * 0.78f, -90f + sweepDeg)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.55f), Color.Transparent),
                    center = head,
                    radius = r * 0.10f
                ),
                radius = r * 0.10f,
                center = head
            )
            drawCircle(color = GoldPale, radius = r * 0.014f, center = head)
        }

        // --- counter-rotating segmented arcs (HUD feel) ---
        val segR = r * 0.64f
        for (i in 0 until 3) {
            val start = -(spin * 360f) + i * 120f
            drawArc(
                color = if (i == 1) Signal.copy(alpha = 0.55f) else Gold.copy(alpha = 0.45f),
                startAngle = start,
                sweepAngle = 58f,
                useCenter = false,
                topLeft = Offset(cx - segR, cy - segR),
                size = androidx.compose.ui.geometry.Size(segR * 2f, segR * 2f),
                style = Stroke(width = r * 0.016f, cap = StrokeCap.Round)
            )
        }

        // --- ping waves from the core ---
        for (i in 0 until 3) {
            val t = (ping + i / 3f) % 1f
            val pr = r * (0.24f + 0.5f * t)
            drawCircle(
                color = Signal.copy(alpha = 0.32f * (1f - t)),
                radius = pr,
                center = Offset(cx, cy),
                style = Stroke(width = r * 0.010f)
            )
        }

        // --- finale bloom as the arena comes up ---
        if (progress > 0.92f) {
            val k = ((progress - 0.92f) / 0.08f).coerceIn(0f, 1f)
            val br = r * (0.30f + 0.70f * k)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(GoldPale.copy(alpha = 0.20f * k), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = br
                ),
                radius = br,
                center = Offset(cx, cy)
            )
        }

        // --- the game mark, holding the core of the dial ---
        val coreR = r * 0.30f * (0.96f + 0.04f * pulse)
        drawQuizesqueMark(
            center = Offset(cx, cy),
            radius = coreR,
            showBindu = true,
            ambient = false
        )

        // --- orbiting satellites with fading trails ---
        val satR = r * 0.50f
        for (i in 0 until 3) {
            val deg = orbit * 360f + i * 120f
            val pos = polar(cx, cy, satR, deg)
            // trail
            for (k in 1..7) {
                val td = deg - k * 5f
                drawLine(
                    color = (if (i == 0) Signal else GoldPale).copy(alpha = 0.26f * (1f - k / 7f)),
                    start = polar(cx, cy, satR, td),
                    end = polar(cx, cy, satR, td - 5f),
                    strokeWidth = r * 0.012f,
                    cap = StrokeCap.Round
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.45f), Color.Transparent),
                    center = pos,
                    radius = r * 0.05f
                ),
                radius = r * 0.05f,
                center = pos
            )
            drawCircle(
                color = if (i == 0) Signal else GoldPale,
                radius = r * 0.016f,
                center = pos
            )
        }

        // --- slow counter-rotating viewfinder brackets ---
        val brR = r * 0.96f
        for (q in 0 until 4) {
            val base = -spin * 120f + q * 90f
            val arm = r * 0.10f
            for (s in 0 until 2) {
                val deg = base + s * 90f
                drawLine(
                    color = Gold.copy(alpha = 0.30f),
                    start = polar(cx, cy, brR, deg),
                    end = polar(cx, cy, brR - arm, deg),
                    strokeWidth = 1.6f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Gold.copy(alpha = 0.30f),
                    start = polar(cx, cy, brR, deg),
                    end = polar(cx, cy, brR, deg + 90f / 4f),
                    strokeWidth = 1.6f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/** Faint engineering grid + a slow scan beam sweeping the backdrop. */
@Composable
private fun BackdropHud(scan: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val step = 34.dp.toPx()
        var x = 0f
        while (x <= size.width) {
            var y = 0f
            while (y <= size.height) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.022f),
                    radius = 1.dp.toPx(),
                    center = Offset(x, y)
                )
                y += step
            }
            x += step
        }
        // scan beam
        val beamY = size.height * scan
        drawRect(
            brush = Brush.verticalGradient(
                listOf(
                    Color.Transparent,
                    Gold.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                startY = beamY - 140f,
                endY = beamY + 140f
            ),
            topLeft = Offset(0f, beamY - 140f),
            size = androidx.compose.ui.geometry.Size(size.width, 280f)
        )
        // corner haze
        drawCircle(
            brush = Brush.radialGradient(
                listOf(GoldDeep.copy(alpha = 0.16f), Color.Transparent),
                center = Offset(size.width * 0.5f, 0f),
                radius = size.width * 0.9f
            ),
            radius = size.width * 0.9f,
            center = Offset(size.width * 0.5f, 0f)
        )
    }
}

/** Chunky tick-meter, brighter near the head so the fill reads as motion. */
@Composable
private fun SegmentedBar(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val segments = 30
        val gap = 3.dp.toPx()
        val w = (size.width - gap * (segments - 1)) / segments
        for (i in 0 until segments) {
            val p = (i + 1f) / segments
            val on = p <= progress
            val near = (progress - p).let { if (it < 0f) 0f else it }
            val col = when {
                !on -> Color.White.copy(alpha = 0.07f)
                near < 0.12f -> GoldPale
                near < 0.30f -> Gold
                else -> Gold.copy(alpha = 0.72f)
            }
            drawRoundRect(
                color = col,
                topLeft = Offset(i * (w + gap), 0f),
                size = androidx.compose.ui.geometry.Size(w, size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f)
            )
        }
    }
}

/** Credit line — the loader's signature. */
@Composable
private fun StudioSignature(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .graphicsLayer { rotationZ = 45f }
                .background(Gold)
        )
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = text,
            color = Muted.copy(alpha = 0.85f),
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
        )
    }
}
