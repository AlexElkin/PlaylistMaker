package com.example.playlistmaker.ui

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.MY_SAVES
import com.example.playlistmaker.data.THEME

class App : Application() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(MY_SAVES, MODE_PRIVATE)
        editor = sharedPreferences.edit()
        darkTheme = if (sharedPreferences.contains(THEME)){sharedPreferences.getBoolean(THEME, darkTheme)} else {checkTheme()}
        switchTheme(darkTheme)
    }

    fun checkTheme(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> false

            Configuration.UI_MODE_NIGHT_YES -> true

            else -> false
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        editor.putBoolean(THEME, darkTheme)
        editor.apply()
    }
}