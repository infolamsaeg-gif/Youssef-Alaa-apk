package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark Palette (Ink, Surfaces & Accents per MY_AGENCY_SPEC)
val ColorInk = Color(0xFF0A0B14)
val ColorSurface = Color(0xFF12141F)
val ColorSurface2 = Color(0xFF1B1E2E)
val ColorSurfaceElevated = Color(0xFF24273A)

// Primary & Secondary Brand Colors
val ColorPrimaryOrange = Color(0xFFFF5B2E) // Fiery Orange
val ColorSecondaryPurple = Color(0xFF6E3BFF) // Electric Purple
val ColorHighlightLime = Color(0xFFC6FF3D) // Neon Lime for accents

// Semantic Colors
val ColorSuccess = Color(0xFF22C55E)
val ColorWarning = Color(0xFFF59E0B)
val ColorDanger = Color(0xFFEF4444)
val ColorInfo = Color(0xFF38BDF8)

// Text Colors (Dark Mode)
val TextPrimaryDark = Color(0xFFF9FAFB)
val TextSecondaryDark = Color(0xFF9CA3AF)
val TextMutedDark = Color(0xFF6B7280)
val BorderDark = Color(0xFF282C40)

// Light Palette
val ColorLightBackground = Color(0xFFF6F5F2)
val ColorLightSurface = Color(0xFFFFFFFF)
val ColorLightSurface2 = Color(0xFFECEAE4)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextMutedLight = Color(0xFF94A3B8)
val BorderLight = Color(0xFFE2E8F0)

// Signature Brand Gradient: Fiery Orange to Electric Purple
val BrandGradient = Brush.horizontalGradient(
    listOf(ColorPrimaryOrange, ColorSecondaryPurple)
)

val BrandGlowGradient = Brush.radialGradient(
    listOf(ColorPrimaryOrange.copy(alpha = 0.35f), Color.Transparent)
)
