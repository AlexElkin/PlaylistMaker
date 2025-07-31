package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.REQUESTING_PLAYBACK_TIME
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.player.impl.PlayerUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerUseCase: PlayerUseCase,
    track: Track
) : ViewModel() {
    private val _playbackState = MutableLiveData<PlaybackState>()
    val playbackState: LiveData<PlaybackState> = _playbackState

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private var updateJob: Job? = null

    init {
        _playbackState.value = PlaybackState.IDLE
        preparePlayer(track.previewUrl)
    }

    private fun preparePlayer(url: String) {
        playerUseCase.preparePlayer(
            url,
            onPrepared = { _playbackState.postValue(PlaybackState.PREPARED) },
            onCompletion = {
                _playbackState.postValue(PlaybackState.COMPLETED)
                _currentPosition.postValue(0)
                stopTimeUpdater()
            }
        )
    }

    fun playbackControl() {
        when (playbackState.value) {
            PlaybackState.PLAYING -> {
                playerUseCase.pause()
                _playbackState.value = PlaybackState.PAUSED
                _currentPosition.value = playerUseCase.getCurrentPosition()
                stopTimeUpdater()
            }
            PlaybackState.PREPARED, PlaybackState.PAUSED -> {
                playerUseCase.seekTo(currentPosition.value ?: 0)
                playerUseCase.play()
                _playbackState.value = PlaybackState.PLAYING
                startTimeUpdater()
            }
            else -> {}
        }
    }

    private fun startTimeUpdater() {
        stopTimeUpdater()
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (playbackState.value == PlaybackState.PLAYING) {
                _currentPosition.postValue(playerUseCase.getCurrentPosition())
                delay(REQUESTING_PLAYBACK_TIME)
            }
        }
    }

    private fun stopTimeUpdater() {
        updateJob?.cancel()
        updateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        playerUseCase.release()
        stopTimeUpdater()
    }

    enum class PlaybackState {
        IDLE, PREPARED, PLAYING, PAUSED, COMPLETED
    }
}