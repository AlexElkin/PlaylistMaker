package com.example.playlistmaker.ui.library.view_model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.ui.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class FragmentTracksViewModel : ViewModel(){
    private val _navigateToPlayer = SingleLiveEvent<Track?>()
    val navigateToPlayer: SingleLiveEvent<Track?> = _navigateToPlayer

    fun onTrackClicked(track: Track) {
        viewModelScope.launch {
            _navigateToPlayer.value = track
        }
    }

    fun onPlayerNavigated() {
        _navigateToPlayer.value = null
    }
}