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
import org.koin.android.ext.android.inject

class MusicService : Service() {

    private val playerRepository: PlayerRepository by inject()
    private val binder = MusicBinder()

    private var currentTrack: Track? = null
    private var isPlaying = false
    private var isForeground = false
    private var currentPosition: Int = 0

    override fun onBind(intent: Intent?): IBinder? = binder

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "music_channel"
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_STOP = "action_stop"
        const val ACTION_STOP_SERVICE = "action_stop_service"
        const val EXTRA_TRACK = "extra_track"
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    private val stateChangeListeners = mutableListOf<(Boolean) -> Unit>()

    fun addStateChangeListener(listener: (Boolean) -> Unit) {
        stateChangeListeners.add(listener)
    }

    fun removeStateChangeListener(listener: (Boolean) -> Unit) {
        stateChangeListeners.remove(listener)
    }

    private fun notifyStateChange() {
        stateChangeListeners.forEach { it(isPlaying) }
    }

    private fun startPlayback(track: Track) {
        currentTrack = track
        isPlaying = true

        playerRepository.preparePlayer(
            track.previewUrl,
            onPrepared = {
                if (currentPosition > 0) {
                    playerRepository.seekTo(currentPosition)
                }
                playerRepository.play()
                notifyStateChange()
            },
            onCompletion = {
                isPlaying = false
                currentPosition = 0
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                notifyStateChange()
            }
        )
    }

    fun showNotification() {
        if (isPlaying && !isForeground) {
            startForeground(NOTIFICATION_ID, createNotification())
            isForeground = true
        }
    }

    private fun pausePlayback() {
        isPlaying = false
        currentPosition = playerRepository.getCurrentPosition()
        playerRepository.pause()
        updateNotification()
        notifyStateChange()
    }

    private fun stopPlayback() {
        isPlaying = false
        currentPosition = 0
        playerRepository.pause()
        seekTo(0)
        updateNotification()
        stopForeground(STOP_FOREGROUND_REMOVE)
        notifyStateChange()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.let {
            when (it.action) {
                ACTION_PLAY -> {
                    val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getParcelableExtra(EXTRA_TRACK, Track::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        it.getParcelableExtra(EXTRA_TRACK)
                    }
                    track?.let { startPlayback(it) }
                }
                ACTION_PAUSE -> pausePlayback()
                ACTION_STOP -> stopPlayback()
                ACTION_STOP_SERVICE -> stopPlayback()
            }
        }

        return START_STICKY
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
    private fun createNotification(): Notification {
        val track = currentTrack ?: return Notification()
        val num = 25
        val artistShort = if (track.artistName.length > num) {
            track.artistName.take(num) + "..."
        } else {
            track.artistName
        }

        val trackNameShort = if (track.trackName.length > num) {
            track.trackName.take(num) + "..."
        } else {
            track.trackName
        }

        val notificationText = "$artistShort - $trackNameShort"

        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("open_player", true)
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
    fun removeNotification() {
        updateNotification()
        stopForeground(STOP_FOREGROUND_REMOVE)
        isForeground = false
        notifyStateChange()
    }
    fun getCurrentPosition(): Int = playerRepository.getCurrentPosition()
    fun seekTo(position: Int) = playerRepository.seekTo(position)
    fun isPlaying(): Boolean = isPlaying
    fun getCurrentTrack(): Track? = currentTrack
    fun isForeground(): Boolean = isForeground
        override fun onDestroy() {
        super.onDestroy()
        playerRepository.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}