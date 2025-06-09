package com.example.playlistmaker.ui.search.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.search.api.SearchInteractor
import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.ui.utils.Debouncer
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val debouncer: Debouncer
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private val _navigateToPlayer = MutableLiveData<Track?>()
    val navigateToPlayer: LiveData<Track?> = _navigateToPlayer

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

    fun performSearch(query: String) {
        viewModelScope.launch {
            try {
                val result = searchInteractor.search(query)
                when (result) {
                    is SearchResult.Success -> {
                        if (result.tracks.isEmpty()) {
                            _state.postValue(SearchState.Empty)
                        } else {
                            _state.postValue(SearchState.Content(result.tracks))
                        }
                    }
                    is SearchResult.Error -> {
                        _state.postValue(
                            when (result.error) {
                                SearchResult.ErrorType.NO_INTERNET -> SearchState.NoInternet
                                else -> SearchState.Error
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                _state.postValue(SearchState.Error)
            }
        }
    }


    fun showHistory() {
        viewModelScope.launch {
            val history = searchInteractor.getSearchHistory()?:mutableListOf()
            if (history.isNotEmpty()) {
                _state.postValue(SearchState.History(history))
            } else {
                _state.postValue(SearchState.Default)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchInteractor.clearSearchHistory()
            _state.postValue(SearchState.Default)
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