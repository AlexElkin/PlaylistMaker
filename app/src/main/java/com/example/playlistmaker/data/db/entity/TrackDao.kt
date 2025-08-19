package com.example.playlistmaker.data.db.entity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.search.Track

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)


    @Query("DELETE FROM tracks_table WHERE preview_url = :previewUrl")
    suspend fun deleteTrackEntity(previewUrl: String)

    @Query("SELECT preview_url FROM tracks_table")
    suspend fun getUrlTrack(): List<String>

    @Query("SELECT * FROM tracks_table")
    suspend fun getTrack(): List<TrackEntity>
}