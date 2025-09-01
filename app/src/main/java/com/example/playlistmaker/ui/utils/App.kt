package com.example.playlistmaker.ui.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.dataModule
import com.example.playlistmaker.databaseModule
import com.example.playlistmaker.debouncer
import com.example.playlistmaker.dispatcherProvider
import com.example.playlistmaker.domain.settings.SettingRepository
import com.example.playlistmaker.playlistsModule
import com.example.playlistmaker.repositoryModule
import com.example.playlistmaker.uiModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin { androidContext(this@App)
                    modules(
                        dispatcherProvider,
                        debouncer,
                        dataModule,
                        repositoryModule,
                        uiModule,
                        playlistsModule,
                        databaseModule
                    )}
        val settingsRepository: SettingRepository by inject()
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