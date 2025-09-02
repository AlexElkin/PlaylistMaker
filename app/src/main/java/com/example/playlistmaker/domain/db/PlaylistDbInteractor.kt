package com.example.playlistmaker.domain.db

import com.example.playlistmaker.data.library.Playlists

interface PlaylistDbInteractor {

    suspend fun getPlaylists(): List<Playlists>
    suspend fun deletePlaylistEntity(id: Int)
    suspend fun insertPlaylist(playlist: Playlists)
    suspend fun getCountTracks(title: String): Int
    suspend fun setCountTracks(title: String,count: Int)
    suspend fun updatePlaylistById(rowId: Int,playlistTitle: String,description: String,picture: String)
    suspend fun getIdPlaylist(title: String): Int
    suspend fun getPlaylistById(id: Int): Playlists
}