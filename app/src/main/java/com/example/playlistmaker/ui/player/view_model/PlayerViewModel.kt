package com.example.playlistmaker.ui.player.view_model


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.data.REQUESTING_PLAYBACK_TIME
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.player.impl.PlayerUseCase

class PlayerViewModel(
    private val playerUseCase: PlayerUseCase,
    private val track: Track
) : ViewModel() {
    private val _playbackState = MutableLiveData<PlaybackState>()
    val playbackState: LiveData<PlaybackState> = _playbackState

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateTimeRunnable: Runnable

    init {
        _playbackState.value = PlaybackState.IDLE
        preparePlayer(track.previewUrl)
        initTimeUpdater()
    }

    fun getTrack(): Track = track

    private fun preparePlayer(url: String) {
        playerUseCase.preparePlayer(
            url,
            onPrepared = {
                _playbackState.postValue(PlaybackState.PREPARED)
            },
            onCompletion = {
                _playbackState.postValue(PlaybackState.COMPLETED)
                _currentPosition.postValue(0)
            }
        )
    }

    fun playbackControl() {
        when (playbackState.value) {
            PlaybackState.PLAYING -> {
                playerUseCase.pause()
                _playbackState.value = PlaybackState.PAUSED
                _currentPosition.value = playerUseCase.getCurrentPosition()
                handler.removeCallbacks(updateTimeRunnable)
            }
            PlaybackState.PREPARED, PlaybackState.PAUSED -> {
                playerUseCase.seekTo(currentPosition.value ?: 0)
                playerUseCase.play()
                _playbackState.value = PlaybackState.PLAYING
                handler.post(updateTimeRunnable)
            }
            else -> {}
        }
    }

    private fun initTimeUpdater() {
        updateTimeRunnable = Runnable {
            if (playbackState.value == PlaybackState.PLAYING) {
                _currentPosition.postValue(playerUseCase.getCurrentPosition())
            }
            handler.postDelayed(updateTimeRunnable, REQUESTING_PLAYBACK_TIME)
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerUseCase.release()
        handler.removeCallbacks(updateTimeRunnable)
    }

    enum class PlaybackState {
        IDLE, PREPARED, PLAYING, PAUSED, COMPLETED
    }
}