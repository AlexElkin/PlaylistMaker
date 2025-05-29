package com.example.playlistmaker.domain.player.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.api.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { onPrepared() }
            mediaPlayer.setOnCompletionListener { onCompletion() }
            mediaPlayer.setOnErrorListener { _, what, extra ->
                // Обработка ошибки
                true
            }
        } catch (e: Exception) {
            // Обработка исключения
        }
    }

    override fun play() = mediaPlayer.start()
    override fun pause() = mediaPlayer.pause()
    override fun seekTo(position: Int) = mediaPlayer.seekTo(position)
    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition
    override fun release() = mediaPlayer.release()
}