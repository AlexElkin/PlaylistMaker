package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistsEntity

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistsEntity)


    @Query("DELETE FROM playlist_table WHERE title = :title")
    suspend fun deletePlaylistEntity(title: String)


    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylistEntity(): List<PlaylistsEntity>

    @Query("SELECT countTracks FROM playlist_table WHERE title = :title")
    suspend fun getCountTracks(title: String): Int

    @Query("UPDATE playlist_table SET countTracks = :count WHERE title = :title")
    suspend fun setCountTracks(title: String,count: Int): Int

    @Query("SELECT id FROM playlist_table WHERE title = :title")
    suspend fun getIdPlaylist(title: String): Int
}