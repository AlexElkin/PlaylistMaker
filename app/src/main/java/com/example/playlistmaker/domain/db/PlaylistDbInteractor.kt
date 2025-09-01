package com.example.playlistmaker.domain.db

import com.example.playlistmaker.data.library.Playlists

interface PlaylistDbInteractor {

    suspend fun getPlaylists(): List<Playlists>
    suspend fun deletePlaylistEntity(title: String)
    suspend fun insertPlaylist(playlist: Playlists)
    suspend fun getCountTracks(title: String): Int
    suspend fun setCountTracks(title: String,count: Int): Int

    suspend fun getIdPlaylist(title: String): Int
}