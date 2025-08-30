package com.example.playlistmaker.domain.db

import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.flow.Flow

interface TracksDbInteractor {

    suspend fun updateStatusTrack(track: Track)
    suspend fun addTrack(track: Track)
    fun getTracks(): Flow<List<Track>>
    fun getTracks(ids: List<Int>): Flow<List<Track>>
    fun getLikeTrack(like: Boolean): Flow<List<Track>>
    suspend fun getIdTrack(previewUrl: String): Int
    suspend fun getStatusTrack(previewUrl: String): Int
    suspend fun setStatusTrack(previewUrl: String,like: Boolean)
}