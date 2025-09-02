package com.example.playlistmaker.data.db.convertor

import com.example.playlistmaker.data.db.entity.PlaylistsEntity
import com.example.playlistmaker.data.library.Playlists

class PlaylistDbConvertor {
    fun mapToPlaylist(playlist: PlaylistsEntity): Playlists {
        return Playlists(
            title = playlist.title,
            picture = playlist.picture,
            countTracks = playlist.countTracks,
            description = playlist.description
        )
    }

    fun mapToPlaylistEntity(playlist: Playlists): PlaylistsEntity {
        return PlaylistsEntity(
            title = playlist.title,
            picture = playlist.picture,
            countTracks = playlist.countTracks,
            description = playlist.description
        )
    }

    fun mapToPlaylistList(playlistsEntity: List<PlaylistsEntity>): List<Playlists> {
        return playlistsEntity.map { mapToPlaylist(it) }
    }

    fun mapToPlaylistEntityList(playlists: List<Playlists>): List<PlaylistsEntity> {
        return playlists.map { mapToPlaylistEntity(it) }
    }
}