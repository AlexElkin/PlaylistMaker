package com.example.playlistmaker.domain.db

import android.util.Log
import com.example.playlistmaker.data.db.TracksDbRepository
import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TracksDbInteractorImpl(private val tracksDbRepository: TracksDbRepository) : TracksDbInteractor {

    override suspend fun getStatusTrack(track: Track): Boolean {
        val listTracksUrl = tracksDbRepository.getUrlTrack().first()
        val status = track.previewUrl in listTracksUrl
        Log.d("тест в итераторе",status.toString())
        return status
    }

    override suspend fun updateStatusTrack(track: Track) {
        when(getStatusTrack(track)){
            true -> tracksDbRepository.deleteTrack(track.previewUrl)
            false -> tracksDbRepository.insertTrack(track)
        }
    }

    override fun getTracks(): Flow<List<Track>> {
        return tracksDbRepository.getTracks()
    }
}