package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/search?entity=song")
    suspend fun getTrack(@Query("term") track: String ): TrackResponse
}