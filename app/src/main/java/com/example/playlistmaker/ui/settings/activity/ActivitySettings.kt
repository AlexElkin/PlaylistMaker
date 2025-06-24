package com.example.playlistmaker.ui.settings.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.main.activity.MainActivity
import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ActivitySettings : AppCompatActivity() {

    private var isSwitchProgrammaticUpdate = false
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()
        observeViewModel()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Вручную обрабатываем изменение темы
        val currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        binding.activitySettingsSwitchTheme.isChecked = isDarkTheme
    }

    private fun setupClickListeners() {
        binding.apply {
            activitySettingsButtonBack.setOnClickListener { viewModel.onBackClicked() }
            activitySettingsTextViewShareTheApp.setOnClickListener { viewModel.onShareAppClicked() }
            activitySettingsTextViewWriteToSupport.setOnClickListener { viewModel.onSupportClicked() }
            activitySettingsTextViewUserAgreement.setOnClickListener { viewModel.onUserAgreementClicked() }
            activitySettingsSwitchTheme.setOnCheckedChangeListener { _, checked ->
                if (!isSwitchProgrammaticUpdate) {
                    Log.d("изменение свитча", "$checked")
                    viewModel.onThemeSwitchChanged(checked)
                }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.themeState.collect { isDarkTheme ->
                    binding.activitySettingsSwitchTheme.isChecked = isDarkTheme
                    updateThemeUi(isDarkTheme)
                }
            }
        }

        viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                is SettingsViewModel.NavigationEvent.Back -> startActivity(Intent(this, MainActivity::class.java))
                is SettingsViewModel.NavigationEvent.ShareApp -> shareApp()
                is SettingsViewModel.NavigationEvent.Support -> openSupportEmail()
                is SettingsViewModel.NavigationEvent.UserAgreement -> openUserAgreement()
            }
        }

        viewModel.toastMessage.observe(this) { message ->
            message?.let { showToast(it) }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, getString(R.string.link_android_developer_plus))
            type = "text/plain"
        }
        startActivity(intent)
    }

    private fun updateThemeUi(isDark: Boolean) {
        binding.root.animate().alpha(0.7f).setDuration(150).withEndAction {
            AppCompatDelegate.setDefaultNightMode(
                if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            delegate.applyDayNight()
            binding.root.animate().alpha(1f).setDuration(150)
        }
    }

    private fun openSupportEmail() {
        try {
            startActivity(Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_mail))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_mail))
            })
        } catch (e: Exception) {
            viewModel.showToast(getString(R.string.no_mail_applications))
        }
    }

    private fun openUserAgreement() {
        startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.link_practicum_offer).toUri()))
    }


}


