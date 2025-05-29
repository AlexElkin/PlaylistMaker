package com.example.playlistmaker.ui.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.SharedPreferencesImpl
import com.example.playlistmaker.data.settings.SettingRepositoryImpl

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val preferencesStorage = SharedPreferencesImpl(this)
        val settingsRepository = SettingRepositoryImpl(preferencesStorage)
        val isDarkTheme = settingsRepository.getTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}