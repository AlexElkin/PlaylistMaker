package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TracksInPlaylistEntity

@Dao
interface TracksInPlaylistDao {

    @Query("SELECT id_track FROM track_playlist_table WHERE id_playlist = :idPlaylist")
    suspend fun getIdTrackInPlaylist(idPlaylist: Int): List<Int>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertIdTrackInPlaylist(tracksInPlaylist: TracksInPlaylistEntity)


}