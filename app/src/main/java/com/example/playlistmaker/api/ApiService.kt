package com.example.playlistmaker.api

import com.example.playlistmaker.data_classes.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/search?entity=song")
    suspend fun getTrack(@Query("term") track: String ): TrackResponse
}