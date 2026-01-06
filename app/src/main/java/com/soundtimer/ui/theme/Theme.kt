package com.soundtimer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTeal,
    onPrimary = Color.White,
    primaryContainer = PrimaryHover.copy(alpha = 0.3f),
    onPrimaryContainer = Color.White,
    secondary = CategoryCalls,
    onSecondary = Color.White,
    tertiary = CategorySystem,
    onTertiary = Color.White,
    surface = SurfaceSlate,
    onSurface = Color.White,
    background = BackgroundMidnight,
    onBackground = Color.White,
    surfaceVariant = SurfaceSlate,
    onSurfaceVariant = TextSlate400,
    outline = BorderSlate
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = Color.White,
    primaryContainer = TealLight,
    onPrimaryContainer = BackgroundMidnight,
    secondary = CategoryCalls,
    onSecondary = Color.White,
    tertiary = CategorySystem,
    surface = CardBackgroundLight,
    onSurface = BackgroundMidnight,
    background = CardBackgroundLight,
    onBackground = BackgroundMidnight,
    surfaceVariant = CardBackgroundLight,
    onSurfaceVariant = BackgroundMidnight
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun SoundTimerTheme(
    darkTheme: Boolean = true, // Default to dark theme for this premium look
    content: @Composable () -> Unit
) {
    // Always use dark theme for the premium aesthetic
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
