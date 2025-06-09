package com.example.playlistmaker

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.playlistmaker.data.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.data.SharedPreferences
import com.example.playlistmaker.data.SharedPreferencesImpl
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.data.search.TrackRepositoryImpl
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.settings.SettingRepositoryImpl
import com.example.playlistmaker.domain.player.api.DispatcherProvider
import com.example.playlistmaker.domain.player.api.PlayerRepository
import com.example.playlistmaker.domain.player.api.TrackRepository
import com.example.playlistmaker.domain.player.impl.DispatcherProviderImpl
import com.example.playlistmaker.domain.player.impl.PlayerRepositoryImpl
import com.example.playlistmaker.domain.player.impl.PlayerUseCase
import com.example.playlistmaker.domain.search.api.SearchHistoryRepository
import com.example.playlistmaker.domain.search.api.SearchInteractor
import com.example.playlistmaker.domain.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.settings.SettingRepository
import com.example.playlistmaker.ui.main.view_model.MainViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.utils.Debouncer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingRepository = module {
    factory<SettingRepository>{ SettingRepositoryImpl(get()) }
}

val sharedPreferences = module {
    factory<SharedPreferences> { SharedPreferencesImpl(get()) }
}

val playerRepository = module {
    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }
}

val playerUseCase = module {
    factory { PlayerUseCase(get()) }
}

val playerModule = module {
    factory { MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    } }
    factory { (track: Track) ->
        PlayerViewModel(get(), track)
    }
}

val searchHistoryRepository = module {
    factory<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }
}

val searchInteractor = module {
    factory<SearchInteractor> { SearchInteractorImpl(get(),get(),get()) }
}

val trackRepository = module {
    factory<TrackRepository> { TrackRepositoryImpl(get()) }
}

val settingsViewModel = module {
    viewModel { SettingsViewModel(get()) }
}

val searchViewModel = module {
    viewModel { SearchViewModel(get(),get()) }
}

val mainViewModel = module {
    viewModel { MainViewModel() }
}

val networkModule = module {
    factory<NetworkClient> {
        RetrofitNetworkClient()
    }
}

val dispatcherProvider = module {
    factory<DispatcherProvider> {
        DispatcherProviderImpl()
    }
}

val debouncer = module {
    factory {
        Debouncer(SEARCH_DEBOUNCE_DELAY)
    }
}