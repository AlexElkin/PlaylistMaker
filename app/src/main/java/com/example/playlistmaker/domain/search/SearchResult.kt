package com.example.playlistmaker.domain.search

import com.example.playlistmaker.data.search.Track

sealed class SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult()
    data class Error(val error: ErrorType) : SearchResult()

    enum class ErrorType {
        NO_INTERNET,
        UNKNOWN
    }
}