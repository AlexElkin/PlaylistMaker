package com.example.playlistmaker.domain.player.api

import com.example.playlistmaker.data.search.Track
// Контракт для репозитория,репозиторий работает напрямую с источниками данных
fun interface TrackRepository {
    suspend fun searchTrack(expression: String): List<Track>
}