package com.example.playlistmaker.data.library

interface PlaylistDbRepository {

    suspend fun getPlaylists(): List<Playlists>
    suspend fun deletePlaylistEntity(title: String)
    suspend fun insertPlaylist(playlist: Playlists)
    suspend fun getCountTracks(title: String): Int
    suspend fun setCountTracks(title: String,count: Int): Int
    suspend fun getIdPlaylist(title: String): Int
}
