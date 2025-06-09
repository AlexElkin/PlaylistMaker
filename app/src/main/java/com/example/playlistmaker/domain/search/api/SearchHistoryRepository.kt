package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.data.search.Track

interface SearchHistoryRepository {
    suspend fun getSearchHistory(): List<Track>?
    suspend fun addToSearchHistory(track: Track)
    suspend fun clearSearchHistory()
}