package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.search.SearchResult

interface SearchInteractor {
    suspend fun search(query: String): SearchResult
    fun isOnline(): Boolean
    suspend fun getSearchHistory(): List<Track>?
    suspend fun addToSearchHistory(track: Track)
    suspend fun clearSearchHistory()
}