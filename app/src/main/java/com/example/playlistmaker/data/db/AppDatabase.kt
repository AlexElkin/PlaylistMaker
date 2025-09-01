package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.dao.TracksInPlaylistDao
import com.example.playlistmaker.data.db.entity.PlaylistsEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.TracksInPlaylistEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistsEntity::class, TracksInPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun tracksInPlaylistDao(): TracksInPlaylistDao

}