package com.example.playlistmaker.data.db.repository

import com.example.playlistmaker.data.db.TracksInPlaylist
import com.example.playlistmaker.data.db.convertor.TracksInPlaylistConvertor
import com.example.playlistmaker.data.db.dao.TracksInPlaylistDao

class TracksInPlaylistRepositoryImpl (private val tracksInPlaylistDao: TracksInPlaylistDao,
                                      private val tracksInPlaylistConvertor: TracksInPlaylistConvertor): TracksInPlaylistRepository{
    override suspend fun getIdTrackInPlaylist(idPlaylist: Int): List<Int> {
        return tracksInPlaylistDao.getIdTrackInPlaylist(idPlaylist)
    }

    override suspend fun insertIdTrackInPlaylist(tracksInPlaylist: TracksInPlaylist) {
        tracksInPlaylistDao.insertIdTrackInPlaylist(tracksInPlaylistConvertor.mapToTracksInPlaylistEntity(tracksInPlaylist))
    }
}