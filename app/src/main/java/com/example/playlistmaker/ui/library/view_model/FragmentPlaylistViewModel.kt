package com.example.playlistmaker.ui.library.view_model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.ui.library.adapter.TrackAdapter
import com.example.playlistmaker.ui.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class FragmentPlaylistViewModel() : ViewModel(), TrackAdapter.OnItemClickListener {



    private val _navigateToPlayer = SingleLiveEvent<Track?>()
    val navigateToPlayer: SingleLiveEvent<Track?> = _navigateToPlayer

    private val _deleteTrack = SingleLiveEvent<Track?>()
    val deleteTrack: SingleLiveEvent<Track?> = _deleteTrack

    fun onPlayerNavigated() {
        _navigateToPlayer.value = null
    }

    fun onNullDeleteTrack() {
        _deleteTrack.value = null
    }

    override fun onItemClick(track: Track) {
        viewModelScope.launch {
            _navigateToPlayer.value = track
        }
    }

    override fun onItemLongClick(track: Track) {
        viewModelScope.launch {
            _deleteTrack.value = track
        }
    }
}

