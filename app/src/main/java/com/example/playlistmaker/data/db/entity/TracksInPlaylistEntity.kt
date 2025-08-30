package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_playlist_table")
data class TracksInPlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "id_track")
    val idTrack: Int,
    @ColumnInfo(name = "id_playlist")
    val idPlaylist: Int)
