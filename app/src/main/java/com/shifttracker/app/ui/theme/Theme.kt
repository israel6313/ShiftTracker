package com.shifttracker.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Background = Color(0xFF1C1C1E)
val Surface = Color(0xFF2C2C2E)
val SurfaceVariant = Color(0xFF3A3A3C)
val Primary = Color(0xFF4A90D9)
val PrimaryVariant = Color(0xFF5B9FE8)
val OnPrimary = Color.White
val OnBackground = Color.White
val OnSurface = Color.White
val TextSecondary = Color(0xFF8E8E93)
val DividerColor = Color(0xFF3A3A3C)
val ErrorColor = Color(0xFFFF453A)
val SuccessColor = Color(0xFF30D158)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    error = ErrorColor
)

@Composable
fun ShiftTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
