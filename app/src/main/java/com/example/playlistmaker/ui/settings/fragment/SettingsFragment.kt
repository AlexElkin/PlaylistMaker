package com.example.playlistmaker.ui.settings.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.settings.jc.SettingsScreen
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import androidx.core.net.toUri

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SettingsScreenWithState()
            }
        }
    }

    @Composable
    private fun SettingsScreenWithState() {
        val isDarkTheme by viewModel.themeState.collectAsState()
        LaunchedEffect(isDarkTheme) {
            applyTheme(isDarkTheme)
        }

        LaunchedEffect(Unit) {
            lifecycleScope.launch {
                viewModel.navigationEvent.collectLatest { event ->
                    when (event) {
                        is SettingsViewModel.NavigationEvent.ShareApp -> shareApp()
                        is SettingsViewModel.NavigationEvent.Support -> openSupportEmail()
                        is SettingsViewModel.NavigationEvent.UserAgreement -> openUserAgreement()
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            lifecycleScope.launch {
                viewModel.toastMessage.collectLatest { message ->
                    showToast(message)
                }
            }
        }

        PlaylistMakerTheme(darkTheme = isDarkTheme) {
            SettingsScreen(
                onShareApp = { viewModel.onShareAppClicked() },
                onSupport = { viewModel.onSupportClicked() },
                onUserAgreement = { viewModel.onUserAgreementClicked() },
                onThemeChanged = { checked ->
                    Log.d("SettingsFragment", "Theme switch changed: $checked")
                    viewModel.onThemeSwitchChanged(checked)
                },
                isDarkTheme = isDarkTheme
            )
        }
    }

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, getString(R.string.link_android_developer_plus))
            type = "text/plain"
        }
        startActivity(intent)
    }

    private fun openSupportEmail() {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:${getString(R.string.email)}".toUri()
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_mail))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_mail))
            }
            startActivity(intent)
        } catch (e: Exception) {
            showToast(getString(R.string.no_mail_applications))
        }
    }

    private fun openUserAgreement() {
        val intent = Intent(Intent.ACTION_VIEW, getString(R.string.link_practicum_offer).toUri())
        startActivity(intent)
    }
}
