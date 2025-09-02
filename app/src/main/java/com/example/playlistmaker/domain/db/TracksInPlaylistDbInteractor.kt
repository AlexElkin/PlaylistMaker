package com.example.playlistmaker.domain.db

import com.example.playlistmaker.data.db.TracksInPlaylist
import com.example.playlistmaker.data.library.Playlists

interface TracksInPlaylistDbInteractor {

    suspend fun countTracksInPlaylist(idPlaylist: Int): Int
    suspend fun deleteTrackEntity(idTrack: Int,idPlaylist: Int)
    suspend fun getIdTrackInPlaylist(idPlaylist: Int): List<Int>
    suspend fun availabilityTracksInPlaylist(idTrack: Int,idPlaylist: Int): Boolean
    suspend fun insertTracksInPlaylist(tracksInPlaylist: TracksInPlaylist)
    suspend fun getSumTracksTime(playlist: Playlists) : String
    suspend fun deletePlaylist(idPlaylist: Int)
}