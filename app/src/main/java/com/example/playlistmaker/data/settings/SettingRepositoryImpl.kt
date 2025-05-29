package com.example.playlistmaker.data.settings

import com.example.playlistmaker.data.SharedPreferences
import com.example.playlistmaker.data.THEME
import com.example.playlistmaker.domain.settings.SettingRepository

class SettingRepositoryImpl (private val  sharedPreferences: SharedPreferences) : SettingRepository {
    override fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME)
    }

    override fun setTheme(isDark: Boolean) {
        sharedPreferences.saveBoolean(THEME,isDark)
    }

}