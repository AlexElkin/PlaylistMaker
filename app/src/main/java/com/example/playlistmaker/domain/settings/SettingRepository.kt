package com.example.playlistmaker.domain.settings

interface SettingRepository {
    fun getTheme(): Boolean
    fun setTheme(isDark: Boolean)
}