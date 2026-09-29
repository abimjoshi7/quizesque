package com.abimatwork.quizesque.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AttatvaColors = darkColorScheme(
    primary = Color(0xFFF3BD55),
    onPrimary = Color(0xFF090B0D),
    primaryContainer = Color(0xFF322718),
    onPrimaryContainer = Color(0xFFFFE2A6),
    secondary = Color(0xFF9AD4C0),
    tertiary = Color(0xFFFF896E),
    background = Color(0xFF090B0D),
    onBackground = Color(0xFFF5F3EF),
    surface = Color(0xFF14181B),
    onSurface = Color(0xFFF5F3EF),
    surfaceVariant = Color(0xFF202528),
    onSurfaceVariant = Color(0xFFADB3B4),
    error = Color(0xFFFF806F),
    errorContainer = Color(0xFF3E211F)
)

@Composable
fun QUIZesqueTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.rgb(9, 11, 13)
            window.navigationBarColor = android.graphics.Color.rgb(9, 11, 13)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }
    MaterialTheme(colorScheme = AttatvaColors, content = content)
}
