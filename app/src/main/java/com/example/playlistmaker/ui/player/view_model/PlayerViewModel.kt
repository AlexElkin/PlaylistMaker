package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.REQUESTING_PLAYBACK_TIME
import com.example.playlistmaker.data.db.TracksInPlaylist
import com.example.playlistmaker.data.library.Playlists
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.db.PlaylistDbInteractor
import com.example.playlistmaker.domain.db.TracksDbInteractor
import com.example.playlistmaker.domain.db.TracksInPlaylistDbInteractor
import com.example.playlistmaker.domain.service.MusicService
import com.example.playlistmaker.ui.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playlistDbInteractor: PlaylistDbInteractor,
    private val tracksDbInteractor: TracksDbInteractor,
    private val tracksInPlaylistDbInteractor: TracksInPlaylistDbInteractor,
    private val track: Track
) : ViewModel() {

    private val _playbackState = MutableLiveData<MusicService.PlaybackState>()
    val playbackState: LiveData<MusicService.PlaybackState> = _playbackState

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private val _showMessage = SingleLiveEvent<String>()
    val showMessage: LiveData<String> = _showMessage

    private var musicServiceController: MusicService.MusicServiceController? = null
    private var timeUpdateJob: Job? = null

    fun setMusicServiceController(controller: MusicService.MusicServiceController) {
        this.musicServiceController = controller
        observePlaybackState()
        forceUpdateState()
    }

    private fun observePlaybackState() {
        viewModelScope.launch {
            musicServiceController?.getPlaybackState()?.collect { state ->
                _playbackState.postValue(state)
                val position = musicServiceController?.getCurrentPosition() ?: 0
                _currentPosition.postValue(position)

                when (state) {
                    MusicService.PlaybackState.PLAYING -> startTimeUpdater()
                    MusicService.PlaybackState.PAUSED,
                    MusicService.PlaybackState.STOPPED,
                    MusicService.PlaybackState.COMPLETED,
                    MusicService.PlaybackState.IDLE -> stopTimeUpdater()
                    else -> {}
                }
            }
        }
    }

    private fun startTimeUpdater() {
        stopTimeUpdater()
        timeUpdateJob = viewModelScope.launch {
            while (musicServiceController?.isPlaying() == true) {
                val position = musicServiceController?.getCurrentPosition() ?: 0
                _currentPosition.postValue(position)
                delay(REQUESTING_PLAYBACK_TIME)

                if (position == 0 && musicServiceController?.isPlaying() != true) {
                    break
                }
            }
        }
    }

    private fun stopTimeUpdater() {
        timeUpdateJob?.cancel()
        timeUpdateJob = null
    }

    private fun forceUpdateState() {
        viewModelScope.launch {
            val currentState = musicServiceController?.getPlaybackState()?.value
            val currentPos = musicServiceController?.getCurrentPosition() ?: 0

            currentState?.let { state ->
                _playbackState.postValue(state)
                _currentPosition.postValue(currentPos)

                if (state == MusicService.PlaybackState.PLAYING) {
                    startTimeUpdater()
                }
            }
        }
    }

    fun playbackControl() {
        when (musicServiceController?.isPlaying()) {
            true -> {
                musicServiceController?.pause()
            }
            false, null -> {
                musicServiceController?.play(track)
            }
        }
    }

    fun showNotification() {
        musicServiceController?.showNotification()
    }

    fun removeNotification() {
        musicServiceController?.removeNotification()
    }

    fun updateCurrentPosition() {
        _currentPosition.value = musicServiceController?.getCurrentPosition() ?: 0
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
                playlistDbInteractor.setCountTracks(playlist.title, playlist.countTracks + 1)
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

    override fun onCleared() {
        super.onCleared()
        stopTimeUpdater()
        musicServiceController = null
    }
}