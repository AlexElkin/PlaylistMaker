package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.flow.Flow

interface TracksDbRepository {

    fun getTracks(): Flow<List<Track>>

    suspend fun deleteTrack(previewUrl: String)

    fun getUrlTrack(): Flow<List<String>>

    suspend fun insertTrack(track: Track)

}