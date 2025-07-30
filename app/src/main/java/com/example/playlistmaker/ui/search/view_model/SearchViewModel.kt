package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.domain.search.api.SearchInteractor
import com.example.playlistmaker.ui.utils.Debouncer
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val debouncer: Debouncer
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private val _navigateToPlayer = MutableLiveData<Track?>()
    val navigateToPlayer: LiveData<Track?> = _navigateToPlayer

    private var searchJob: Job? = null

    fun searchDebounced(query: String) {
        if (query.isEmpty()) {
            showHistory()
            return
        }

        _state.value = SearchState.Loading
        debouncer.debounce {
            performSearch(query)
        }

    }

    internal fun performSearch(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            println("SEARCH_STARTED: $query")
            try {val result = searchInteractor.search(query)
                println(result)
                when (result) {
                    is SearchResult.Success -> {
                        println("+")
                        result.tracks.collect { tracks ->
                            println("SEARCH_STARTED3: $tracks")
                            _state.value = if (tracks.isEmpty()) {
                                SearchState.Empty
                            } else {
                                SearchState.Content(tracks)
                            }
                        }
                    }
                    is SearchResult.Error -> {
                        _state.value = when (result.error) {
                            SearchResult.ErrorType.NO_INTERNET -> SearchState.NoInternet
                            SearchResult.ErrorType.UNKNOWN -> SearchState.Error
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = SearchState.Error
            }
        }
    }

    fun showHistory() {
        viewModelScope.launch {
            val history = searchInteractor.getSearchHistory() ?: emptyList()
            _state.value = if (history.isNotEmpty()) {
                SearchState.History(history)
            } else {
                SearchState.Default
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchInteractor.clearSearchHistory()
            _state.value = SearchState.Default
        }
    }

    fun onTrackClicked(track: Track) {
        viewModelScope.launch {
            searchInteractor.addToSearchHistory(track)
            _navigateToPlayer.value = track
        }
    }

    fun onPlayerNavigated() {
        _navigateToPlayer.value = null
    }
}

sealed class SearchState {
    object Default : SearchState()
    object Loading : SearchState()
    object Empty : SearchState()
    object NoInternet : SearchState()
    object Error : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
}