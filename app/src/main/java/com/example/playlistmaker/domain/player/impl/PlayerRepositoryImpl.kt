package com.example.playlistmaker.domain.player.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.api.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {

    private var isPrepared = false
    private var isReleased = false

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        try {
            isReleased = false
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                isPrepared = true
                onPrepared()
            }
            mediaPlayer.setOnCompletionListener {
                isPrepared = false
                onCompletion()
            }
            mediaPlayer.setOnErrorListener { _, what, extra ->
                isPrepared = false
                true
            }
        } catch (e: Exception) {
            isPrepared = false
        }
    }

    override fun play() {
        if (!isReleased && isPrepared && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun pause() {
        if (!isReleased && isPrepared && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun seekTo(position: Int) {
        if (!isReleased && isPrepared) {
            mediaPlayer.seekTo(position)
        }
    }

    override fun getCurrentPosition(): Int {
        return if (!isReleased && isPrepared) {
            mediaPlayer.currentPosition
        } else {
            0
        }
    }

    override fun release() {
        if (!isReleased) {
            isReleased = true
            isPrepared = false
            mediaPlayer.release()
        }
    }

    override fun isPlaying(): Boolean {
        return !isReleased && isPrepared && mediaPlayer.isPlaying
    }
}