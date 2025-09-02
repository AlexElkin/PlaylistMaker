package com.example.playlistmaker.data.library

import android.util.Log
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

    override suspend fun setCountTracks(title: String, count: Int) {
        playlistDao.setCountTracks(title,count)
    }

    override suspend fun updatePlaylistById(
        rowId: Int,
        playlistTitle: String,
        description: String,
        picture: String
    ) {
        playlistDao.updatePlaylistById(
            rowId = rowId,
            playlistTitle = playlistTitle,
            description = description,
            picture = picture
        )
    }

    override suspend fun getIdPlaylist(title: String): Int {
        return playlistDao.getIdPlaylist(title)
    }

    override suspend fun getPlaylistById(id: Int): Playlists {
        val entity = playlistDao.getPlaylistById(id)
            ?: throw IllegalArgumentException("Playlist with id $id not found")
        return playlistDbConvertor.mapToPlaylist(entity)
    }

    override suspend fun getPlaylists(): List<Playlists> {
        return playlistDbConvertor.mapToPlaylistList(playlistDao.getPlaylistEntity())
    }

    override suspend fun deletePlaylistEntity(id: Int) {
        Log.d("PlaylistDebug", "Deleting playlist with id: $id")
        playlistDao.deletePlaylistEntity(id)
        Log.d("PlaylistDebug", "Playlist deleted successfully")
    }

}