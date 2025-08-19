package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.db.convertor.TrackDbConvertor
import com.example.playlistmaker.data.db.entity.TrackDao
import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksDbRepositoryImpl (
    private val trackDao: TrackDao,
    private val trackDbConvertor: TrackDbConvertor,
) : TracksDbRepository {

    override fun getTracks(): Flow<List<Track>> = flow {
        val trackEntities = trackDao.getTrack()
        val tracks = trackDbConvertor.mapToTrackList(trackEntities)
        emit(tracks)
    }

    override suspend fun deleteTrack(previewUrl: String) {
        trackDao.deleteTrackEntity(previewUrl)
    }

    override fun getUrlTrack(): Flow<List<String>> = flow {
        val urls = trackDao.getUrlTrack()
        emit(urls)
    }

    override suspend fun insertTrack(track: Track) {

        trackDao.insertTrack(trackDbConvertor.mapToTrackEntity(track))
    }

}