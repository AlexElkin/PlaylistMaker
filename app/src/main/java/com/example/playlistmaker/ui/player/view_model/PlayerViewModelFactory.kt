package com.example.playlistmaker.ui.player.view_model





import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.player.impl.PlayerUseCase

class PlayerViewModelFactory(
    private val playerUseCase: PlayerUseCase,
    private val track: Track
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(playerUseCase, track) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}