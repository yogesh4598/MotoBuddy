package com.example.motobuddy.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MotoBuddyColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = MidnightNavy,
    primaryContainer = CardBackground,
    onPrimaryContainer = ElectricCyan,
    secondary = VibrantOrange,
    onSecondary = MidnightNavy,
    secondaryContainer = CardBackground,
    onSecondaryContainer = VibrantOrange,
    background = MidnightNavy,
    onBackground = TextPrimary,
    surface = DarkGraphite,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    outline = DividerBorderColor,
    outlineVariant = DividerBorderColor,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun MotoBuddyTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = MotoBuddyColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
