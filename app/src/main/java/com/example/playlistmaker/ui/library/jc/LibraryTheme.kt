package com.example.playlistmaker.ui.library.jc

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LibraryLightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color.White,
    onSurfaceVariant = Color(0xFF666666),
    background = Color.White,
    onBackground = Color.Black
)

private val LibraryDarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color.Black,
    onSurfaceVariant = Color.White,
    background = Color.Black,
    onBackground = Color.White
)

@Composable
fun LibraryTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = if (isSystemInDarkTheme()) {
        LibraryDarkColorScheme
    } else {
        LibraryLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}