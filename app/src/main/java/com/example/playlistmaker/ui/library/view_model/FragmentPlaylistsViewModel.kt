package com.example.playlistmaker.ui.library.view_model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.library.Playlists
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.db.PlaylistDbInteractor
import com.example.playlistmaker.domain.db.TracksInPlaylistDbInteractor
import com.example.playlistmaker.ui.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class FragmentPlaylistsViewModel() : ViewModel(){

    private val _navigateToPlayer = SingleLiveEvent<Playlists?>()
    val navigateToPlayer: SingleLiveEvent<Playlists?> = _navigateToPlayer
    fun onPlaylistClicked(playlist: Playlists) {
        viewModelScope.launch {
            _navigateToPlayer.value = playlist
        }
    }

    fun onPlayerNavigated() {
        _navigateToPlayer.value = null
    }
}