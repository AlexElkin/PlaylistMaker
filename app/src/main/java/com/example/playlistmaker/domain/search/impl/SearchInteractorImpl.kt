package com.example.playlistmaker.domain.search.impl



import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import com.example.playlistmaker.data.search.impl.NoInternetException
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.player.api.DispatcherProvider
import com.example.playlistmaker.data.search.api.TrackRepository
import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.data.search.api.SearchHistoryRepository
import com.example.playlistmaker.domain.search.api.SearchInteractor
import kotlinx.coroutines.withContext

class SearchInteractorImpl(
    private val context: Context,
    private val searchRepository: TrackRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val dispatcherProvider: DispatcherProvider
) : SearchInteractor {

    override suspend fun search(query: String): SearchResult {
        return withContext(dispatcherProvider.io) {
            try {
                val tracks = searchRepository.searchTrack(query)
                SearchResult.Success(tracks)
            } catch (e: Exception) {
                SearchResult.Error(
                    if (e is NoInternetException) {
                        SearchResult.ErrorType.NO_INTERNET
                    } else {
                        SearchResult.ErrorType.UNKNOWN
                    }
                )
            }
        }
    }

    override fun isOnline(): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getSearchHistory(): List<Track>? {
        return searchHistoryRepository.getSearchHistory()
    }

    override suspend fun addToSearchHistory(track: Track) {
        searchHistoryRepository.addToSearchHistory(track)
    }

    override suspend fun clearSearchHistory() {
        searchHistoryRepository.clearSearchHistory()
    }
}