package com.example.playlistmaker.domain.search

import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.flow.Flow

sealed interface SearchResult {
    data class Success(val tracks: Flow<List<Track>>) : SearchResult
    data class Error(val error: ErrorType) : SearchResult

    enum class ErrorType {
        NO_INTERNET,
        UNKNOWN
    }
}