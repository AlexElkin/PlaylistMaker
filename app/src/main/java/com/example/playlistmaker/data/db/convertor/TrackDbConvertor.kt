package com.example.playlistmaker.data.db.convertor

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.search.Track

class TrackDbConvertor {
    fun mapToTrack(track: TrackEntity): Track {
        return Track(
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            trackTimeMillis = track.trackTimeMillis,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            artworkUrl100 = track.artworkUrl100,
            previewUrl = track.previewUrl
        )
    }

    fun mapToTrackEntity(track: Track): TrackEntity {
        return TrackEntity(
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            trackTimeMillis = track.trackTimeMillis,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            artworkUrl100 = track.artworkUrl100,
            previewUrl = track.previewUrl,
            like = false)
    }

    fun mapToTrackList(trackEntities: List<TrackEntity>): List<Track> {
        return trackEntities.map { mapToTrack(it) }
    }

    fun mapToTrackEntityList(tracks: List<Track>): List<TrackEntity> {
        return tracks.map { mapToTrackEntity(it) }
    }
}