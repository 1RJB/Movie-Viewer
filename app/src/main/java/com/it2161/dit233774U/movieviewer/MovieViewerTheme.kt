package com.it2161.dit233774U.movieviewer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFA31D1D), // Dark Shade
    onPrimary = Color.White,
    primaryContainer = Color(0xFF6D2323), // Darkest Shade
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFE5D0AC), // Light Shade
    onSecondary = Color.Black,
    background = Color(0xFFFEF9E1), // Lightest Shade
    onBackground = Color.Black,
    surface = Color(0xFFFFFBF0),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFFFFBF0),
    onSurfaceVariant = Color(0xFF333333),
    outline = Color(0xFF808080),
    inverseSurface = Color(0xFF404040),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE5D0AC), // Light Shade
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFFEF9E1), // Lightest Shade
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF6D2323), // Darkest Shade
    onSecondary = Color.White,
    background = Color(0xFF121212), // Dark background
    onBackground = Color.White,
    surfaceVariant = Color(0xFF303030),
    onSurfaceVariant = Color(0xFFFFFBF0),
    outline = Color(0xFF808080),
    inverseSurface = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E), // Dark surface
    onSurface = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun MovieViewerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
