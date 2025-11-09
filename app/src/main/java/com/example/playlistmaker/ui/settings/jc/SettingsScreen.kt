package com.example.playlistmaker.ui.settings.jc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.playlistmaker.R

@Composable
fun SettingsScreen(
    onShareApp: () -> Unit,
    onSupport: () -> Unit,
    onUserAgreement: () -> Unit,
    onThemeChanged: (Boolean) -> Unit,
    isDarkTheme: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .clickable { onThemeChanged(!isDarkTheme) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.dark_theme),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                color = MaterialTheme.colorScheme.onBackground
            )

            Switch(
                checked = isDarkTheme,
                onCheckedChange = onThemeChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color("#3772E7".toColorInt()),
                    uncheckedThumbColor = Color("#AEAFB4".toColorInt()),
                    checkedTrackColor = Color("#9FBBF3".toColorInt()),
                    uncheckedTrackColor = Color("#E6E8EB".toColorInt()),
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = Color.Transparent,
                    disabledCheckedThumbColor = Color.Transparent,
                    disabledUncheckedThumbColor = Color.Transparent,
                    disabledCheckedTrackColor = Color.Transparent,
                    disabledUncheckedTrackColor = Color.Transparent,
                    disabledCheckedBorderColor = Color.Transparent,
                    disabledUncheckedBorderColor = Color.Transparent
                )
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            thickness = 1.dp
        )
        SettingsItem(
            text = stringResource(R.string.share_the_app),
            icon = R.drawable.to_share,
            onClick = onShareApp,
            isDarkTheme = isDarkTheme
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            thickness = 1.dp
        )
        SettingsItem(
            text = stringResource(R.string.write_to_support),
            icon = R.drawable.support,
            onClick = onSupport,
            isDarkTheme = isDarkTheme
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            thickness = 1.dp
        )
        SettingsItem(
            text = stringResource(R.string.user_agreement),
            icon = R.drawable.arrow,
            onClick = onUserAgreement,
            isDarkTheme = isDarkTheme
        )
    }
}

@Composable
fun SettingsItem(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val iconColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.onSurface
    } else {
        Color(0xFFAEAFB4)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            color = MaterialTheme.colorScheme.onBackground
        )

        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconColor
        )
    }
}