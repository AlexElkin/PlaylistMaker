package com.example.playlistmaker.data.db.repository

import com.example.playlistmaker.data.db.TracksInPlaylist
import com.example.playlistmaker.data.db.convertor.TracksInPlaylistConvertor
import com.example.playlistmaker.data.db.dao.TracksInPlaylistDao

class TracksInPlaylistRepositoryImpl (private val tracksInPlaylistDao: TracksInPlaylistDao,
                                      private val tracksInPlaylistConvertor: TracksInPlaylistConvertor): TracksInPlaylistRepository{
    override suspend fun getIdTrackInPlaylist(idPlaylist: Int): List<Int> {
        return tracksInPlaylistDao.getIdTrackInPlaylist(idPlaylist)
    }

    override suspend fun deleteTrackEntity(idTrack: Int) {
        tracksInPlaylistDao.deleteTrackEntity(idTrack)
    }

    override suspend fun insertIdTrackInPlaylist(tracksInPlaylist: TracksInPlaylist) {
        tracksInPlaylistDao.insertIdTrackInPlaylist(tracksInPlaylistConvertor.mapToTracksInPlaylistEntity(tracksInPlaylist))
    }

    override suspend fun deletePlaylist(idPlaylist: Int) {
        tracksInPlaylistDao.deletePlaylist(idPlaylist)
    }
}