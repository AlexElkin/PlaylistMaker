package com.example.playlistmaker.data.db.repository

import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.flow.Flow

interface TracksDbRepository {

    fun getTracks(): Flow<List<Track>>
    fun getTracks(ids: List<Int>): Flow<List<Track>>
    fun getLikeTrack(like: Boolean): Flow<List<Track>>
    suspend fun deleteTrack(previewUrl: String)
    fun getUrlTrack(): Flow<List<String>>
    suspend fun insertTrack(track: Track)
    suspend fun getIdTrack(previewUrl: String): Int
    suspend fun getStatusTrack(previewUrl: String): Boolean?
    suspend fun setStatusTrack(previewUrl: String,like: Boolean)
}