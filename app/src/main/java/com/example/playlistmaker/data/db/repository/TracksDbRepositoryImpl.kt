package com.example.playlistmaker.data.db.repository

import com.example.playlistmaker.data.db.convertor.TrackDbConvertor
import com.example.playlistmaker.data.db.dao.TrackDao
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

    override fun getTracks(ids: List<Int>): Flow<List<Track>> = flow {
        val trackEntities = trackDao.getTrack(ids)
        val tracks = trackDbConvertor.mapToTrackList(trackEntities)
        emit(tracks)
    }

    override fun getLikeTrack(like: Boolean): Flow<List<Track>> = flow {
        val trackEntities = trackDao.getLikeTrack(like)
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

    override suspend fun getIdTrack(previewUrl: String): Int {
        return trackDao.getIdTrack(previewUrl)
    }

    override suspend fun getStatusTrack(previewUrl: String): Boolean? {
        return trackDao.getStatusTrack(previewUrl)
    }

    override suspend fun getSumTimeTrack(ids: List<Int>): Long {
        return trackDao.getSumTimeTrack(ids)
    }

    override suspend fun setStatusTrack(previewUrl: String, like: Boolean) {
        trackDao.setStatusTrack(
            previewUrl = previewUrl,
            like = like
        )
    }

}