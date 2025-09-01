package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertTrack(track: TrackEntity)


    @Query("DELETE FROM tracks_table WHERE preview_url = :previewUrl")
    suspend fun deleteTrackEntity(previewUrl: String)

    @Query("SELECT preview_url FROM tracks_table")
    suspend fun getUrlTrack(): List<String>

    @Query("SELECT * FROM tracks_table")
    suspend fun getTrack(): List<TrackEntity>

    @Query("SELECT * FROM tracks_table WHERE id IN (:ids)")
    suspend fun getTrack(ids: List<Int>): List<TrackEntity>

    @Query("SELECT * FROM tracks_table WHERE `like` = :like")
    suspend fun getLikeTrack(like: Boolean): List<TrackEntity>

    @Query("SELECT id FROM tracks_table WHERE preview_url = :previewUrl")
    suspend fun getIdTrack(previewUrl: String): Int

    @Query("SELECT `like` FROM tracks_table WHERE preview_url = :previewUrl")
    suspend fun getStatusTrack(previewUrl: String): Boolean?

    @Query("UPDATE tracks_table SET `like` = :like WHERE preview_url = :previewUrl")
    suspend fun setStatusTrack(previewUrl: String,like: Boolean)

}