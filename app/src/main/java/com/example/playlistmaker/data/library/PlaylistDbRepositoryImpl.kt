package com.example.playlistmaker.data.library

import com.example.playlistmaker.data.db.convertor.PlaylistDbConvertor
import com.example.playlistmaker.data.db.dao.PlaylistDao

class PlaylistDbRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
) : PlaylistDbRepository {
    override suspend fun insertPlaylist(playlist: Playlists) {
        playlistDao.insertPlaylist(playlistDbConvertor.mapToPlaylistEntity(playlist))
    }

    override suspend fun getCountTracks(title: String): Int {
        return playlistDao.getCountTracks(title)
    }

    override suspend fun setCountTracks(title: String, count: Int): Int {
        return playlistDao.setCountTracks(title,count)
    }

    override suspend fun getIdPlaylist(title: String): Int {
        return playlistDao.getIdPlaylist(title)
    }

    override suspend fun getPlaylists(): List<Playlists> {
        return playlistDbConvertor.mapToPlaylistList(playlistDao.getPlaylistEntity())
    }

    override suspend fun deletePlaylistEntity(title: String) {
        playlistDao.deletePlaylistEntity(title)
    }

}