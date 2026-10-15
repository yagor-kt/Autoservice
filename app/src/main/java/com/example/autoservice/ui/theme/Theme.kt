package com.example.autoservice.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppDarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurface,
    error = Error,
    onError = OnPrimary
)

@Composable
fun AutoServiceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}