package com.nova.workouttracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7DA7FF),
    onPrimary = Color(0xFF0A1A3D),
    primaryContainer = Color(0xFF1D4ED8),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF9FB3D9),
    background = Color(0xFF0B0F16),
    surface = Color(0xFF141B28),
    surfaceVariant = Color(0xFF1C2536),
    onBackground = Color(0xFFE8EDF5),
    onSurface = Color(0xFFE8EDF5),
    onSurfaceVariant = Color(0xFFA9B4C7),
    outline = Color(0xFF2E3A4F)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCE6FF),
    onPrimaryContainer = Color(0xFF0A1A3D),
    secondary = Color(0xFF475569),
    background = Color(0xFFF4F6F8),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE8EDF5),
    onBackground = Color(0xFF1C2530),
    onSurface = Color(0xFF1C2530),
    onSurfaceVariant = Color(0xFF5B6B82),
    outline = Color(0xFFD6DEE9)
)

@Composable
fun WorkoutTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
