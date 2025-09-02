package com.example.playlistmaker.domain.db

import android.util.Log
import com.example.playlistmaker.data.db.TracksInPlaylist
import com.example.playlistmaker.data.db.repository.TracksDbRepository
import com.example.playlistmaker.data.db.repository.TracksInPlaylistRepository
import com.example.playlistmaker.data.library.PlaylistDbRepository
import com.example.playlistmaker.data.library.Playlists

class TracksInPlaylistDbInteractorImpl(
    private val tracksInPlaylistRepository: TracksInPlaylistRepository,
    private val playlistDbRepository: PlaylistDbRepository,
    private val tracksDbRepository: TracksDbRepository
) : TracksInPlaylistDbInteractor {
    override suspend fun countTracksInPlaylist(idPlaylist: Int): Int {
        return tracksInPlaylistRepository.getIdTrackInPlaylist(idPlaylist).size
    }

    override suspend fun deleteTrackEntity(idTrack: Int, idPlaylist: Int) {
        tracksInPlaylistRepository.deleteTrackEntity(idTrack)
        val playlist = playlistDbRepository.getPlaylistById(idPlaylist)
        playlistDbRepository.setCountTracks(playlist.title, playlist.countTracks - 1)
    }

    override suspend fun getIdTrackInPlaylist(idPlaylist: Int): List<Int> {
        return tracksInPlaylistRepository.getIdTrackInPlaylist(idPlaylist)
    }

    override suspend fun availabilityTracksInPlaylist(idTrack: Int, idPlaylist: Int): Boolean {
        return idTrack in tracksInPlaylistRepository.getIdTrackInPlaylist(idPlaylist)
    }

    override suspend fun insertTracksInPlaylist(tracksInPlaylist: TracksInPlaylist) {
        tracksInPlaylistRepository.insertIdTrackInPlaylist(tracksInPlaylist)
    }

    override suspend fun getSumTracksTime(playlist: Playlists): String {
        return formatTrackTime(
            tracksDbRepository.getSumTimeTrack(
                tracksInPlaylistRepository.getIdTrackInPlaylist(
                    playlistDbRepository.getIdPlaylist(playlist.title)
                )
            )
        )
    }

    override suspend fun deletePlaylist(idPlaylist: Int) {
        Log.d("PlaylistDebug", "Starting deletion of playlist id: $idPlaylist")
        tracksInPlaylistRepository.deletePlaylist(idPlaylist)
        Log.d("PlaylistDebug", "Tracks deleted from playlist")
        playlistDbRepository.deletePlaylistEntity(idPlaylist)
        Log.d("PlaylistDebug", "Playlist entity deleted")
    }

    private fun formatTrackTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
