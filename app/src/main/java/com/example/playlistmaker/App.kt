package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(MY_SAVES,MODE_PRIVATE)
        editor = sharedPreferences.edit()
        darkTheme = sharedPreferences.getBoolean(THEME,darkTheme)
        switchTheme(darkTheme)
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
        editor.putBoolean(THEME,darkTheme)
        editor.apply()
    }
}