package com.example.playlistmaker.data.db.repository

import com.example.playlistmaker.data.db.TracksInPlaylist

interface TracksInPlaylistRepository {
    suspend fun getIdTrackInPlaylist(idPlaylist: Int): List<Int>
    suspend fun deleteTrackEntity(idTrack: Int)
    suspend fun insertIdTrackInPlaylist(tracksInPlaylist: TracksInPlaylist)
    suspend fun deletePlaylist(idPlaylist: Int)
}