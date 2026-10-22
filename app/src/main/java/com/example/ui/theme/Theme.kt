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

private val DarkColorScheme = darkColorScheme(
    primary = ColorPrimaryOrange,
    onPrimary = ColorInk,
    primaryContainer = ColorPrimaryOrange.copy(alpha = 0.2f),
    onPrimaryContainer = ColorPrimaryOrange,
    secondary = ColorSecondaryPurple,
    onSecondary = ColorLightBackground,
    secondaryContainer = ColorSecondaryPurple.copy(alpha = 0.2f),
    onSecondaryContainer = ColorSecondaryPurple,
    tertiary = ColorHighlightLime,
    background = ColorInk,
    onBackground = TextPrimaryDark,
    surface = ColorSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ColorSurface2,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    error = ColorDanger,
    onError = ColorLightBackground
)

private val LightColorScheme = lightColorScheme(
    primary = ColorPrimaryOrange,
    onPrimary = ColorLightBackground,
    primaryContainer = ColorPrimaryOrange.copy(alpha = 0.15f),
    onPrimaryContainer = ColorPrimaryOrange,
    secondary = ColorSecondaryPurple,
    onSecondary = ColorLightBackground,
    secondaryContainer = ColorSecondaryPurple.copy(alpha = 0.15f),
    onSecondaryContainer = ColorSecondaryPurple,
    tertiary = ColorHighlightLime,
    background = ColorLightBackground,
    onBackground = TextPrimaryLight,
    surface = ColorLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = ColorLightSurface2,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    error = ColorDanger,
    onError = ColorLightBackground
)

@Composable
fun MyAgencyTheme(
    darkTheme: Boolean = true, // Default to Luxury Dark Mode per spec
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.surface.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
