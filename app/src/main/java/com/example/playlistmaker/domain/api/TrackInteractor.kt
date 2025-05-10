package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.dto.Track
// Контракт для бизнес-логики,обрабатывает данные
fun interface TrackInteractor {
    suspend fun searchTrack(expression: String): List<Track>
}