package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.search.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    suspend fun search(query: String): SearchResult
    suspend fun getSearchHistory(): List<Track>?
    suspend fun addToSearchHistory(track: Track)
    suspend fun clearSearchHistory()
}