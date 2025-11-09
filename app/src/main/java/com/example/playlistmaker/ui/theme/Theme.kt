package com.example.playlistmaker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.example.playlistmaker.R

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val primaryColor = Color(ResourcesCompat.getColor(context.resources, R.color.blue, context.theme))
    val backgroundColor = if (darkTheme) {
        Color(ResourcesCompat.getColor(context.resources, R.color.black, context.theme))
    } else {
        Color(ResourcesCompat.getColor(context.resources, R.color.white, context.theme))
    }
    val textColor = if (darkTheme) {
        Color(ResourcesCompat.getColor(context.resources, R.color.white, context.theme))
    } else {
        Color(ResourcesCompat.getColor(context.resources, R.color.black, context.theme))
    }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = textColor,
            background = backgroundColor,
            onBackground = textColor,
            surface = backgroundColor,
            onSurface = textColor,
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = textColor,
            background = backgroundColor,
            onBackground = textColor,
            surface = backgroundColor,
            onSurface = textColor,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}