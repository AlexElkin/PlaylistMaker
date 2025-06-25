package com.example.playlistmaker.ui.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.SharedPreferencesImpl
import com.example.playlistmaker.data.settings.SettingRepositoryImpl
import com.example.playlistmaker.debouncer
import com.example.playlistmaker.dispatcherProvider
import com.example.playlistmaker.domain.settings.SettingRepository
import com.example.playlistmaker.fragmentPlaylistsViewModel
import com.example.playlistmaker.fragmentTracksViewModel
import com.example.playlistmaker.libraryViewModel
import com.example.playlistmaker.mainViewModel
import com.example.playlistmaker.networkModule
import com.example.playlistmaker.playerModule
import com.example.playlistmaker.playerRepository
import com.example.playlistmaker.playerUseCase
import com.example.playlistmaker.searchHistoryRepository
import com.example.playlistmaker.searchInteractor
import com.example.playlistmaker.searchViewModel
import com.example.playlistmaker.settingRepository
import com.example.playlistmaker.settingsViewModel
import com.example.playlistmaker.sharedPreferences
import com.example.playlistmaker.trackRepository
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin { androidContext(this@App)
                    modules(
                        playerUseCase,
                        searchHistoryRepository,
                        searchInteractor,
                        playerRepository,
                        sharedPreferences,
                        settingRepository,
                        trackRepository,
                        playerModule,
                        settingsViewModel,
                        searchViewModel,
                        mainViewModel,
                        networkModule,
                        dispatcherProvider,
                        debouncer,
                        libraryViewModel,
                        fragmentPlaylistsViewModel,
                        fragmentTracksViewModel
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