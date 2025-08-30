package com.example.playlistmaker.data.search.api

import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.flow.Flow

// Контракт для репозитория,репозиторий работает напрямую с источниками данных
fun interface TrackRepository {
    suspend fun searchTrack(expression: String): Flow<List<Track>>
}