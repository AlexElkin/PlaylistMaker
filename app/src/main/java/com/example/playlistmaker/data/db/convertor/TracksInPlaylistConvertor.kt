package com.example.playlistmaker.data.db.convertor

import com.example.playlistmaker.data.db.TracksInPlaylist
import com.example.playlistmaker.data.db.entity.TracksInPlaylistEntity

class TracksInPlaylistConvertor {
    fun mapToTracksInPlaylist(tracksInPlaylistEntity: TracksInPlaylistEntity): TracksInPlaylist {
        return TracksInPlaylist(
            idTrack = tracksInPlaylistEntity.idTrack,
            idPlaylist = tracksInPlaylistEntity.idPlaylist
        )
    }

    fun mapToTracksInPlaylistEntity(tracksInPlaylist: TracksInPlaylist): TracksInPlaylistEntity {
        return TracksInPlaylistEntity(
            idTrack = tracksInPlaylist.idTrack,
            idPlaylist = tracksInPlaylist.idPlaylist)
    }

    fun mapToTracksInPlaylistList(tracksInPlaylistEntity: List<TracksInPlaylistEntity>): List<TracksInPlaylist> {
        return tracksInPlaylistEntity.map { mapToTracksInPlaylist(it) }
    }

    fun mapToTracksInPlaylistEntityList(tracksInPlaylist: List<TracksInPlaylist>): List<TracksInPlaylistEntity> {
        return tracksInPlaylist.map { mapToTracksInPlaylistEntity(it) }
    }
}