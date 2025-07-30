package com.example.playlistmaker.domain.search.impl



import com.example.playlistmaker.data.search.NoInternetException
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.player.api.DispatcherProvider
import com.example.playlistmaker.domain.player.api.TrackRepository
import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.domain.search.api.SearchHistoryRepository
import com.example.playlistmaker.domain.search.api.SearchInteractor
import kotlinx.coroutines.withContext

class SearchInteractorImpl(
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