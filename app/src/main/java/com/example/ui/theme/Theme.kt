package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ReelDarkColorScheme = darkColorScheme(
    primary = ReelMagenta,
    onPrimary = TextWhite,
    primaryContainer = ReelPurple,
    onPrimaryContainer = TextWhite,
    secondary = ReelOrange,
    onSecondary = TextWhite,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextWhite,
    tertiary = NeonCyan,
    onTertiary = DarkBg,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = DarkCardBorder,
    outlineVariant = DarkCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek dark Instagram studio aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = ReelDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
