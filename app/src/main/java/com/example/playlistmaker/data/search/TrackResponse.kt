package com.example.playlistmaker.data.search

data class TrackResponse(
    val results: List<Track>
) : Response()