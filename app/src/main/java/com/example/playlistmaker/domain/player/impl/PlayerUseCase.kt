package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.api.PlayerRepository

class PlayerUseCase(private val repository: PlayerRepository) {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) =
        repository.preparePlayer(url, onPrepared, onCompletion)
    fun play() = repository.play()
    fun pause() = repository.pause()
    fun seekTo(position: Int) = repository.seekTo(position)
    fun getCurrentPosition(): Int = repository.getCurrentPosition()
    fun release() = repository.release()
}