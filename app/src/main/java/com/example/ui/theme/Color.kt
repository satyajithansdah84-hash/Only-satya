package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Instagram Reel Brand Gradients & Accents
val ReelMagenta = Color(0xFFE1306C)
val ReelRed = Color(0xFFFD1D1D)
val ReelOrange = Color(0xFFF77737)
val ReelPurple = Color(0xFF833AB4)
val ReelBlue = Color(0xFF405DE6)
val ReelYellow = Color(0xFFFFDC80)

// Pro Gold Accents
val ProGold = Color(0xFFFFD700)
val ProGoldLight = Color(0xFFFFF099)
val ProGoldDark = Color(0xFFC79500)

// Dark Theme Surfaces
val DarkBg = Color(0xFF0C0C0E)
val DarkSurface = Color(0xFF16161B)
val DarkSurfaceVariant = Color(0xFF21212B)
val DarkCard = Color(0xFF1E1E26)
val DarkCardBorder = Color(0xFF2E2E3C)

// Typography & Content
val TextWhite = Color(0xFFF5F5F7)
val TextMuted = Color(0xFF9E9EA8)
val TextDim = Color(0xFF6B6B78)

// Neon Accents
val NeonCyan = Color(0xFF00F0FF)
val NeonPink = Color(0xFFFF007F)
val NeonGreen = Color(0xFF00E676)

// Instagram Gradient
val InstagramGradient = Brush.linearGradient(
    colors = listOf(ReelPurple, ReelMagenta, ReelOrange, ReelYellow)
)

val ProGoldGradient = Brush.linearGradient(
    colors = listOf(ProGoldLight, ProGold, ProGoldDark)
)

val DarkSurfaceGradient = Brush.verticalGradient(
    colors = listOf(DarkSurface, DarkBg)
)
