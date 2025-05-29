package com.example.playlistmaker.ui.settings.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.data.settings.SettingRepositoryImpl
import com.example.playlistmaker.data.SharedPreferencesImpl
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.main.activity.MainActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModelFactory
import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel


class ActivitySettings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private  val check by lazy { viewModel.themeState.value ?: checkTheme() }
    private val viewModel: SettingsViewModel by lazy {
        val preferencesStorage = SharedPreferencesImpl(this)
        val settingsRepository = SettingRepositoryImpl(preferencesStorage)
        val viewModelFactory = SettingsViewModelFactory(settingsRepository)
        ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.activitySettingsSwitchTheme.isChecked = check
        Log.d("изменение дефолтные свитча","$check")
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.apply {
            activitySettingsButtonBack.setOnClickListener { viewModel.onBackClicked() }
            activitySettingsTextViewShareTheApp.setOnClickListener { viewModel.onShareAppClicked() }
            activitySettingsTextViewWriteToSupport.setOnClickListener { viewModel.onSupportClicked() }
            activitySettingsTextViewUserAgreement.setOnClickListener { viewModel.onUserAgreementClicked() }

            activitySettingsSwitchTheme.setOnCheckedChangeListener { _, checked ->
                if (checked != check){
                Log.d("изменение свитча","$checked")
                viewModel.onThemeSwitchChanged(checked)
                //updateThemeUi(checked)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.themeState.observe(this) { isDarkTheme ->//если изменились данные
            Log.d("изменение темы","$isDarkTheme")
            updateThemeUi(isDarkTheme)
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

    private fun updateThemeUi(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun checkTheme(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> false

            Configuration.UI_MODE_NIGHT_YES -> true

            else -> false
        }
    }
}


