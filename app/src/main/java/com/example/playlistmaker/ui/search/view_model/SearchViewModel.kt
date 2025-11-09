package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.domain.search.api.SearchInteractor
import com.example.playlistmaker.ui.utils.Debouncer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val debouncer: Debouncer
) : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Default)
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _navigateToPlayer = MutableStateFlow<Track?>(null)
    val navigateToPlayer: StateFlow<Track?> = _navigateToPlayer.asStateFlow()
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private var searchJob: Job? = null
    private var currentQuery: String = ""

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun searchDebounced(query: String) {
        searchJob?.cancel()
        currentQuery = query
        _searchText.value = query

        if (query.isEmpty()) {
            showHistory()
            return
        } else {
            debouncer.debounce {
                _state.value = SearchState.Loading
                performSearch(query)
            }
        }
    }

    fun clearSearch() {
        _searchText.value = ""
        currentQuery = ""
        showHistory()
    }
    fun isOnline(): Boolean {
        return searchInteractor.isOnline()
    }

    internal fun performSearch(query: String) {
        if (!searchInteractor.isOnline()) {
            _state.value = SearchState.NoInternet
            return
        }

        if (query != currentQuery || query.trim().isEmpty()) {
            if (currentQuery.isEmpty()) {
                showHistory()
            }
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            try {
                _state.value = SearchState.Loading

                val result = searchInteractor.search(query)

                if (query != currentQuery) {
                    return@launch
                }

                when (result) {
                    is SearchResult.Success -> {
                        result.tracks.collect { tracks ->
                            if (query == currentQuery) {
                                _state.value = if (tracks.isEmpty()) {
                                    SearchState.Empty
                                } else {
                                    SearchState.Content(tracks)
                                }
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
                if (query == currentQuery) {
                    if (!searchInteractor.isOnline()) {
                        _state.value = SearchState.NoInternet
                    } else {
                        _state.value = SearchState.Error
                    }
                }
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

    fun onPlayerNavigated() {
        _navigateToPlayer.value = null
    }

    fun onTrackClick(track: Track) {
        viewModelScope.launch {
            searchInteractor.addToSearchHistory(track)
            _navigateToPlayer.value = track
        }
    }

    fun getCurrentQuery(): String = currentQuery
}

sealed class SearchState {
    object Default : SearchState()
    object Loading : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
    object NoInternet : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
}