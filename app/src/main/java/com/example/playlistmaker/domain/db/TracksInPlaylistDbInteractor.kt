package com.example.playlistmaker.domain.db

import com.example.playlistmaker.data.db.TracksInPlaylist

interface TracksInPlaylistDbInteractor {

    suspend fun countTracksInPlaylist(idPlaylist: Int): Int
    suspend fun getIdTrackInPlaylist(idPlaylist: Int): List<Int>
    suspend fun availabilityTracksInPlaylist(idTrack: Int,idPlaylist: Int): Boolean
    suspend fun insertTracksInPlaylist(tracksInPlaylist: TracksInPlaylist)
}