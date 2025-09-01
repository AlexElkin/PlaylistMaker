package com.example.playlistmaker.domain.db

import com.example.playlistmaker.data.db.TracksInPlaylist
import com.example.playlistmaker.data.db.repository.TracksInPlaylistRepository

class TracksInPlaylistDbInteractorImpl(private val tracksInPlaylistRepository: TracksInPlaylistRepository) : TracksInPlaylistDbInteractor {
    override suspend fun countTracksInPlaylist(idPlaylist: Int): Int {
        return tracksInPlaylistRepository.getIdTrackInPlaylist(idPlaylist).size
    }

    override suspend fun getIdTrackInPlaylist(idPlaylist: Int): List<Int> {
        return tracksInPlaylistRepository.getIdTrackInPlaylist(idPlaylist)
    }

    override suspend fun availabilityTracksInPlaylist(idTrack: Int,idPlaylist: Int): Boolean {
        return idTrack in tracksInPlaylistRepository.getIdTrackInPlaylist(idPlaylist)
    }

    override suspend fun insertTracksInPlaylist(tracksInPlaylist: TracksInPlaylist) {
        tracksInPlaylistRepository.insertIdTrackInPlaylist(tracksInPlaylist)
    }
}
