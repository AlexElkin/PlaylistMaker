package com.example.playlistmaker.ui.search.jc


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.example.playlistmaker.R

@Composable
fun SearchTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val searchColorScheme = if (darkTheme) {
        darkColorScheme().copy(
            primary = Color(ResourcesCompat.getColor(context.resources, R.color.blue, context.theme)),
            onPrimary = Color.White,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFFF5F5F5),
            onSurface = Color.Black,
            surfaceVariant = Color(0xFFEEEEEE),
            onSurfaceVariant = Color(0xFF666666),
            outline = Color(0xFFE0E0E0),

            onPrimaryContainer = Color.Unspecified,
            onSecondaryContainer = Color.Unspecified,
            onTertiaryContainer = Color.Unspecified
        )
    } else {
        lightColorScheme().copy(
            primary = Color(ResourcesCompat.getColor(context.resources, R.color.blue, context.theme)),
            onPrimary = Color.White,
            background = Color.White,
            onBackground = Color.Black,
            surface = Color(0xFFF8F8F8),
            onSurface = Color.Black,
            surfaceVariant = Color(0xFFF0F0F0),
            onSurfaceVariant = Color(0xFFAEAFB4),
            outline = Color(0xFFE0E0E0),

            onPrimaryContainer = Color.Unspecified,
            onSecondaryContainer = Color.Unspecified,
            onTertiaryContainer = Color.Unspecified
        )
    }

    MaterialTheme(
        colorScheme = searchColorScheme,
        content = content
    )
}