package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.db.convertor.TrackDbConvertor
import com.example.playlistmaker.data.db.entity.AppDatabase
import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksDbRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : TracksDbRepository {

    override fun getTracks(): Flow<List<Track>> = flow {
        val trackEntities = appDatabase.trackDao().getTrack()
        val tracks = trackDbConvertor.mapToTrackList(trackEntities)
        emit(tracks)
    }

    override suspend fun deleteTrack(previewUrl: String) {
        appDatabase.trackDao().deleteTrackEntity(previewUrl)
    }

    override fun getUrlTrack(): Flow<List<String>> = flow {
        val urls = appDatabase.trackDao().getUrlTrack()
        emit(urls)
    }

    override suspend fun insertTrack(track: Track) {

        appDatabase.trackDao().insertTrack(trackDbConvertor.mapToTrackEntity(track))
    }

}