package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.dto.Track
// Контракт для репозитория,репозиторий работает напрямую с источниками данных
interface TrackRepository {
    suspend fun searchTrack(expression: String): List<Track>
}