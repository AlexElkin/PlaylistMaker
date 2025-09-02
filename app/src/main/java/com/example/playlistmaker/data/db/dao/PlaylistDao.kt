package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistsEntity
import com.example.playlistmaker.data.library.Playlists

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistsEntity)
    @Query("DELETE FROM playlist_table WHERE id = :rowId")
    suspend fun deletePlaylistEntity(rowId: Int)
    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylistEntity(): List<PlaylistsEntity>
    @Query("SELECT countTracks FROM playlist_table WHERE title = :playlistTitle")
    suspend fun getCountTracks(playlistTitle: String): Int
    @Query("UPDATE playlist_table SET countTracks = :count WHERE title = :playlistTitle")
    suspend fun setCountTracks(playlistTitle: String,count: Int)
    @Query("UPDATE playlist_table SET title = :playlistTitle, description = :description,picture = :picture WHERE  id = :rowId")
    suspend fun updatePlaylistById(rowId: Int,playlistTitle: String,description: String,picture: String)
    @Query("SELECT id FROM playlist_table WHERE title = :playlistTitle")
    suspend fun getIdPlaylist(playlistTitle: String): Int
    @Query("SELECT * FROM playlist_table WHERE id = :id")
    suspend fun getPlaylistById(id: Int): PlaylistsEntity?
}