package com.example.playlistmaker.domain.player.api

interface PlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun seekTo(position: Int)
    fun getCurrentPosition(): Int
    fun release()

    fun isPlaying(): Boolean
}