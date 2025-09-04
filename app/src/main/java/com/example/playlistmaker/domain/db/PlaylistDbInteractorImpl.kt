package com.example.playlistmaker.domain.db


import com.example.playlistmaker.data.library.PlaylistDbRepository
import com.example.playlistmaker.data.library.Playlists

class PlaylistDbInteractorImpl(private val playlistDbRepository: PlaylistDbRepository) : PlaylistDbInteractor {

    override suspend fun getPlaylists(): List<Playlists> {
        return playlistDbRepository.getPlaylists()
    }

    override suspend fun deletePlaylistEntity(id: Int) {
        playlistDbRepository.deletePlaylistEntity(id)
    }

    override suspend fun insertPlaylist(playlist: Playlists) {
        playlistDbRepository.insertPlaylist(playlist)
    }

    override suspend fun getCountTracks(title: String): Int {
        return playlistDbRepository.getCountTracks(title)
    }

    override suspend fun setCountTracks(title: String, count: Int) {
        playlistDbRepository.setCountTracks(title,count)
    }

    override suspend fun updatePlaylistById(
        rowId: Int,
        playlistTitle: String,
        description: String,
        picture: String
    ) {
        playlistDbRepository.updatePlaylistById(
            rowId = rowId,
            playlistTitle = playlistTitle,
            description = description,
            picture = picture
        )
    }

    override suspend fun getIdPlaylist(title: String): Int {
        return playlistDbRepository.getIdPlaylist(title)
    }

    override suspend fun getPlaylistById(id: Int): Playlists{
        return playlistDbRepository.getPlaylistById(id)
    }
}