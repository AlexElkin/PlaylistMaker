package com.example.playlistmaker.ui.search.view_model


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.data.SharedPreferences
import com.example.playlistmaker.data.SharedPreferencesImpl
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.TrackRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.player.api.DispatcherProvider
import com.example.playlistmaker.domain.player.api.TrackRepository
import com.example.playlistmaker.domain.player.impl.DispatcherProviderImpl
import com.example.playlistmaker.domain.search.api.SearchHistoryRepository
import com.example.playlistmaker.domain.search.api.SearchInteractor
import com.example.playlistmaker.domain.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.example.playlistmaker.ui.utils.Debouncer

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(
                searchInteractor = provideSearchInteractor(context),
                debouncer = Debouncer(SEARCH_DEBOUNCE_DELAY)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    private fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(
            searchRepository = provideSearchRepository(),
            searchHistoryRepository = provideSearchHistoryRepository(context),
            dispatcherProvider = getDispatcherProvider()
        )
    }

    private fun provideSearchRepository(): TrackRepository {
        return TrackRepositoryImpl(getNetworkClient())
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(getSharedPreferences(context))
    }

    private fun getNetworkClient(): NetworkClient {
        return RetrofitNetworkClient()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return SharedPreferencesImpl(context)
    }

    private fun getDispatcherProvider(): DispatcherProvider {
        return DispatcherProviderImpl()
    }



    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}