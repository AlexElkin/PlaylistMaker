package com.example.playlistmaker.domain.db

import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.flow.Flow

interface TracksDbInteractor {

    suspend fun getStatusTrack(track: Track): Boolean

    suspend fun updateStatusTrack(track: Track)

    fun getTracks(): Flow<List<Track>>

}