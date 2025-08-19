package com.example.playlistmaker.ui.library.view_model
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.launch

class FragmentTracksViewModel : ViewModel(){
    private val _navigateToPlayer = MutableLiveData<Track?>()
    val navigateToPlayer: LiveData<Track?> = _navigateToPlayer

    fun onTrackClicked(track: Track) {
        viewModelScope.launch {
            _navigateToPlayer.value = track
        }
    }

    fun onPlayerNavigated() {
        _navigateToPlayer.value = null
    }
}