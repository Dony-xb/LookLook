package com.looklook.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = AccentPink,
    secondary = AccentBlue,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface
)

@Composable
fun LookLookTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
