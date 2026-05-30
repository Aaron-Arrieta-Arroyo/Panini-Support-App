package com.panini.supportapp.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PaniniBlue = Color(0xFF1565C0)
private val PaniniGreen = Color(0xFF2E7D32)
private val PaniniAmber = Color(0xFFF57F17)
private val PaniniRed = Color(0xFFC62828)

private val AppColorScheme = lightColorScheme(
    primary = PaniniBlue,
    secondary = PaniniGreen,
    tertiary = PaniniAmber,
    error = PaniniRed
)

@Composable
fun SupportAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}
