package com.example.playlistmaker.domain.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.player.api.PlayerRepository
import com.example.playlistmaker.ui.main.activity.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.koin.android.ext.android.inject

class MusicService : Service() {

    private val playerRepository: PlayerRepository by inject()

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = MusicBinder()

    private var currentTrack: Track? = null
    private var isPlaying = false
    private var isForeground = false
    private var currentPosition: Int = 0

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState


    interface MusicServiceController {
        fun play(track: Track)
        fun pause()
        fun stop()
        fun getCurrentPosition(): Int
        fun isPlaying(): Boolean
        fun getCurrentTrack(): Track?
        fun showNotification()
        fun removeNotification()
        fun getPlaybackState(): StateFlow<PlaybackState>
    }

    private val controller = object : MusicServiceController {
        override fun play(track: Track) {
            startPlayback(track)
        }

        override fun pause() {
            pausePlayback()
        }

        override fun stop() {
            stopPlayback()
        }

        override fun getCurrentPosition(): Int = this@MusicService.getCurrentPosition()
        override fun isPlaying(): Boolean = this@MusicService.isPlaying()
        override fun getCurrentTrack(): Track? = this@MusicService.getCurrentTrack()
        override fun showNotification() = this@MusicService.showNotification()
        override fun removeNotification() = this@MusicService.removeNotification()
        override fun getPlaybackState(): StateFlow<PlaybackState> = this@MusicService.playbackState
    }

    override fun onBind(intent: Intent?): IBinder {
        currentTrack = getTrackFromIntent(intent)
        return binder
    }

    private fun getTrackFromIntent(intent: Intent?): Track? {
        return intent?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelableExtra(EXTRA_TRACK, Track::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelableExtra(EXTRA_TRACK)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun startPlayback(track: Track) {
        if (currentTrack?.previewUrl == track.previewUrl && _playbackState.value == PlaybackState.PAUSED) {
            isPlaying = true
            playerRepository.play()
            if (currentPosition > 0) {
                playerRepository.seekTo(currentPosition)
            }
            _playbackState.update { PlaybackState.PLAYING }
            updateNotification()
            return
        }
        currentTrack = track
        isPlaying = true
        currentPosition = 0

        _playbackState.update { PlaybackState.PREPARING }

        playerRepository.preparePlayer(
            track.previewUrl,
            onPrepared = {
                playerRepository.play()
                isPlaying = true
                _playbackState.update { PlaybackState.PLAYING }
                updateNotification()
            },
            onCompletion = {
                isPlaying = false
                currentPosition = 0
                _playbackState.update { PlaybackState.COMPLETED }
                updateNotification()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        )
    }

    private fun pausePlayback() {
        if (isPlaying) {
            isPlaying = false
            currentPosition = playerRepository.getCurrentPosition()
            playerRepository.pause()
            _playbackState.update { PlaybackState.PAUSED }
            updateNotification()
        }
    }

    private fun stopPlayback() {
        isPlaying = false
        currentPosition = 0
        playerRepository.pause()
        playerRepository.seekTo(0)
        _playbackState.update { PlaybackState.STOPPED }
        updateNotification()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.let {
            when (it.action) {
                ACTION_PLAY -> {
                    val track = getTrackFromIntent(it) ?: currentTrack
                    track?.let { startPlayback(it) }
                }
                ACTION_PAUSE -> pausePlayback()
                ACTION_STOP -> stopPlayback()
                ACTION_STOP_SERVICE -> stopPlayback()
                else -> {
                    updateStateFromCurrent()
                }
            }
        } ?: run {
            updateStateFromCurrent()
        }

        return START_STICKY
    }

    private fun updateStateFromCurrent() {
        when {
            isPlaying -> _playbackState.update { PlaybackState.PLAYING }
            currentTrack != null -> _playbackState.update { PlaybackState.PAUSED }
            else -> _playbackState.update { PlaybackState.IDLE }
        }
        updateNotification()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Music Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Music playback controls"
            setShowBadge(false)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification() {
        if (!isForeground) {
            startForeground(NOTIFICATION_ID, createNotification())
            isForeground = true
        }
    }

    fun removeNotification() {
        if (isForeground) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            isForeground = false
        }
    }

    private fun createNotification(): Notification {
        val track = currentTrack ?: return Notification()

        val notificationText = "${track.artistName} - ${track.trackName}"

        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("open_player", true)
            putExtra(EXTRA_TRACK, track)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                R.drawable.track_pause,
                "Pause",
                getPendingIntent(ACTION_PAUSE, 2)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.track_play,
                "Play",
                getPendingIntent(ACTION_PLAY, 1)
            )
        }

        val stopAction = NotificationCompat.Action(
            R.drawable.stop,
            "Stop",
            getPendingIntent(ACTION_STOP, 3)
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.media_library)
            .setContentTitle("Playlist Maker")
            .setContentText(notificationText)
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .addAction(playPauseAction)
            .addAction(stopAction)

        return builder.build()
    }

    private fun getPendingIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
            currentTrack?.let { putExtra(EXTRA_TRACK, it) }
        }
        return PendingIntent.getService(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun updateNotification() {
        if (isForeground) {
            val notification = createNotification()
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    fun getCurrentPosition(): Int {
        return if (isPlaying) {
            playerRepository.getCurrentPosition()
        } else {
            currentPosition
        }
    }

    fun seekTo(position: Int) = playerRepository.seekTo(position)
    fun isPlaying(): Boolean = isPlaying
    fun getCurrentTrack(): Track? = currentTrack
    fun isForeground(): Boolean = isForeground
    fun getController(): MusicServiceController = controller

    override fun onDestroy() {
        super.onDestroy()
        playerRepository.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
        _playbackState.update { PlaybackState.IDLE }
    }

    enum class PlaybackState {
        IDLE, PREPARING, PLAYING, PAUSED, STOPPED, COMPLETED
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "music_channel"
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_STOP = "action_stop"
        const val ACTION_STOP_SERVICE = "action_stop_service"
        const val EXTRA_TRACK = "extra_track"
    }
}