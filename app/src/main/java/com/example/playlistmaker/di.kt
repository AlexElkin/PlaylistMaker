package com.example.playlistmaker

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.data.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.data.SharedPreferences
import com.example.playlistmaker.data.SharedPreferencesImpl
import com.example.playlistmaker.data.db.TracksDbRepository
import com.example.playlistmaker.data.db.convertor.TrackDbConvertor
import com.example.playlistmaker.data.db.entity.AppDatabase
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.data.search.TrackRepositoryImpl
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.settings.SettingRepositoryImpl
import com.example.playlistmaker.domain.db.TracksDbInteractor
import com.example.playlistmaker.data.db.TracksDbRepositoryImpl
import com.example.playlistmaker.domain.db.TracksDbInteractorImpl
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
import com.example.playlistmaker.ui.library.view_model.FragmentPlaylistsViewModel
import com.example.playlistmaker.ui.library.view_model.FragmentTracksViewModel
import com.example.playlistmaker.ui.main.view_model.MainViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.viewmodel.LibraryViewModel
import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.utils.Debouncer
import kotlinx.coroutines.MainScope
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    factory<SharedPreferences> { SharedPreferencesImpl(get()) }
    factory<NetworkClient> {RetrofitNetworkClient()}
    single {Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
        .build()
} }

val repositoryModule = module {

    factory { TrackDbConvertor() }
    factory<SettingRepository>{ SettingRepositoryImpl(get()) }
    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }
    factory { PlayerUseCase(get()) }
    factory<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }
    factory<TrackRepository> { TrackRepositoryImpl(get()) }
    factory<SearchInteractor> { SearchInteractorImpl(get(),get(),get()) }
    single<TracksDbRepository> {
        TracksDbRepositoryImpl(get(), get())
    }
    single<TracksDbInteractor> {
        TracksDbInteractorImpl(get())
    }
}

val uiModule = module {
    viewModel { SettingsViewModel(get()) }
    viewModel { SearchViewModel(get(),get()) }
    viewModel { MainViewModel() }
    viewModel { LibraryViewModel() }
    viewModel { FragmentPlaylistsViewModel() }
    viewModel { FragmentTracksViewModel() }
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


val dispatcherProvider = module {
    factory<DispatcherProvider> {
        DispatcherProviderImpl()
    }
}

val debouncer = module {
    factory { Debouncer(SEARCH_DEBOUNCE_DELAY, MainScope() ) }
}