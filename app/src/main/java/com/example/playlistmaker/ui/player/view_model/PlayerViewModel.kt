package com.example.playlistmaker.ui.player.view_model

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
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
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playlistDbInteractor: PlaylistDbInteractor,
    private val tracksDbInteractor: TracksDbInteractor,
    private val tracksInPlaylistDbInteractor: TracksInPlaylistDbInteractor,
    private val context: Context,
    private val track: Track
) : ViewModel() {

    private val _playbackState = MutableLiveData<PlaybackState>()
    val playbackState: LiveData<PlaybackState> = _playbackState

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private var updateJob: Job? = null

    private val _showMessage = SingleLiveEvent<String>()
    val showMessage: LiveData<String> = _showMessage

    private var musicService: MusicService? = null
    private var isBound = false
    val isForeground: Boolean
        get() = musicService?.isForeground() ?: false

    fun showNotification() {
        musicService?.showNotification()
    }

    private val stateChangeListener: (Boolean) -> Unit = { isPlaying ->
        viewModelScope.launch {
            if (isPlaying) {
                _playbackState.value = PlaybackState.PLAYING
                startTimeUpdater()
            } else {
                _playbackState.value = PlaybackState.PAUSED
                _currentPosition.value = musicService?.getCurrentPosition() ?: 0
                stopTimeUpdater()
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true

            musicService?.addStateChangeListener(stateChangeListener)
            updateStateFromService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService?.removeStateChangeListener(stateChangeListener)
            musicService = null
            isBound = false
            _playbackState.value = PlaybackState.IDLE
            _currentPosition.value = 0
            stopTimeUpdater()
        }
    }

    init {
        _playbackState.value = PlaybackState.IDLE
        bindMusicService()
    }

    private fun bindMusicService() {
        try {
            val intent = Intent(context, MusicService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.IDLE
        }
    }

    private fun updateStateFromService() {
        if (!isBound || musicService == null) {
            _playbackState.value = PlaybackState.IDLE
            _currentPosition.value = 0
            stopTimeUpdater()
            return
        }

        when {
            musicService?.isPlaying() == true -> {
                _playbackState.value = PlaybackState.PLAYING
                startTimeUpdater()
            }
            musicService?.getCurrentTrack() != null -> {
                _playbackState.value = PlaybackState.PAUSED
                _currentPosition.value = musicService?.getCurrentPosition() ?: 0
                stopTimeUpdater()
            }
            else -> {
                _playbackState.value = PlaybackState.IDLE
                _currentPosition.value = 0
                stopTimeUpdater()
            }
        }
    }

    fun stopService() {
        stopTimeUpdater()

        if (isBound) {
            musicService?.removeStateChangeListener(stateChangeListener)
            val intent = Intent(context, MusicService::class.java).apply {
                action = MusicService.ACTION_STOP_SERVICE
            }
            context.startService(intent)
            context.unbindService(serviceConnection)
            isBound = false
        }

        musicService = null
        _playbackState.value = PlaybackState.IDLE
        _currentPosition.value = 0
    }

    fun stopForegroundService(){
        musicService?.removeNotification()
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

    fun playbackControl() {
        val intent = Intent(context, MusicService::class.java).apply {
            when (playbackState.value) {
                PlaybackState.PLAYING -> action = MusicService.ACTION_PAUSE
                PlaybackState.PAUSED -> {
                    action = MusicService.ACTION_PLAY
                    putExtra(MusicService.EXTRA_TRACK, track)
                }
                PlaybackState.IDLE, PlaybackState.COMPLETED -> {
                    action = MusicService.ACTION_PLAY
                    putExtra(MusicService.EXTRA_TRACK, track)
                }
                else -> {
                    action = MusicService.ACTION_PLAY
                    putExtra(MusicService.EXTRA_TRACK, track)
                }
            }
        }
        context.startService(intent)
    }

    private fun startTimeUpdater() {
        stopTimeUpdater()
        updateJob = viewModelScope.launch {
            while (playbackState.value == PlaybackState.PLAYING && isBound) {
                val position = musicService?.getCurrentPosition() ?: 0
                _currentPosition.postValue(position)
                delay(REQUESTING_PLAYBACK_TIME)

                if (position == 0 && musicService?.isPlaying() == false) {
                    _playbackState.postValue(PlaybackState.COMPLETED)
                    break
                }
            }
        }
    }

    private fun stopTimeUpdater() {
        updateJob?.cancel()
        updateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        musicService?.removeStateChangeListener(stateChangeListener)
        stopService()
        stopTimeUpdater()
    }

    enum class PlaybackState {
        IDLE, PLAYING, PAUSED, COMPLETED
    }
}