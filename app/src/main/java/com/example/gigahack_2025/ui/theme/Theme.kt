package com.example.gigahack_2025.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = FigmaPrimaryBlue,
    secondary = FigmaBlue,
    tertiary = FigmaLightBlueAccent,
    background = FigmaBlack,
    surface = FigmaDarkGray2,
    onPrimary = FigmaWhite,
    onSecondary = FigmaWhite,
    onTertiary = FigmaDarkBlue,
    onBackground = FigmaWhite,
    onSurface = FigmaWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = FigmaPrimaryBlue,
    secondary = FigmaBlue,
    tertiary = FigmaLightBlueAccent,
    background = FigmaWhite,
    surface = FigmaWhite,
    onPrimary = FigmaWhite,
    onSecondary = FigmaDarkBlue,
    onTertiary = FigmaDarkBlue,
    onBackground = FigmaDarkBlue,
    onSurface = FigmaDarkBlue,
)

@Composable
fun GIGAHACK_2025Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}