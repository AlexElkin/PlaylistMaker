package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.REQUESTING_PLAYBACK_TIME
import com.example.playlistmaker.data.db.TracksInPlaylist
import com.example.playlistmaker.data.library.Playlists
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.db.PlaylistDbInteractor
import com.example.playlistmaker.domain.db.TracksDbInteractor
import com.example.playlistmaker.domain.db.TracksInPlaylistDbInteractor
import com.example.playlistmaker.domain.player.impl.PlayerUseCase
import com.example.playlistmaker.ui.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerUseCase: PlayerUseCase,
    private val playlistDbInteractor: PlaylistDbInteractor,
    private val tracksDbInteractor: TracksDbInteractor,
    private val tracksInPlaylistDbInteractor: TracksInPlaylistDbInteractor,
    track: Track
) : ViewModel() {
    private val _playbackState = MutableLiveData<PlaybackState>()
    val playbackState: LiveData<PlaybackState> = _playbackState

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private var updateJob: Job? = null
    private val track = track

    private val _showMessage = SingleLiveEvent<String>()
    val showMessage: LiveData<String> = _showMessage

    init {
        _playbackState.value = PlaybackState.IDLE
        preparePlayer(track.previewUrl)
    }

    fun onPlaylistClicked(playlist: Playlists) {
        viewModelScope.launch {
            tracksDbInteractor.addTrack(track)
            val idTrack = tracksDbInteractor.getIdTrack(track.previewUrl)
            val idPlaylist = playlistDbInteractor.getIdPlaylist(playlist.title)
            if (!tracksInPlaylistDbInteractor.availabilityTracksInPlaylist(
                    idTrack = idTrack,
                    idPlaylist = idPlaylist
                )
            ) {
                playlistDbInteractor.setCountTracks(playlist.title,playlist.countTracks+1)
                tracksInPlaylistDbInteractor.insertTracksInPlaylist(
                    TracksInPlaylist(
                        idTrack = tracksDbInteractor.getIdTrack(track.previewUrl),
                        idPlaylist = playlistDbInteractor.getIdPlaylist(playlist.title)
                    )
                )
                _showMessage.value = "Добавлено в плейлист ${playlist.title}"
            } else _showMessage.value = "Трек уже добавлен в плейлист ${playlist.title}"
        }
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