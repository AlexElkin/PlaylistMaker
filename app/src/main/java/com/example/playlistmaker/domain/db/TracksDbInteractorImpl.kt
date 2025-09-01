package com.example.playlistmaker.domain.db


import com.example.playlistmaker.data.db.repository.TracksDbRepository
import com.example.playlistmaker.data.search.Track
import kotlinx.coroutines.flow.Flow

class TracksDbInteractorImpl(private val tracksDbRepository: TracksDbRepository) :
    TracksDbInteractor {


    override suspend fun updateStatusTrack(track: Track) {
        when (getStatusTrack(track.previewUrl)) {
            1 -> tracksDbRepository.setStatusTrack(track.previewUrl, false)
            0 -> tracksDbRepository.setStatusTrack(track.previewUrl, true)
            else -> {
                tracksDbRepository.insertTrack(track)
                tracksDbRepository.setStatusTrack(track.previewUrl, true)
            }
        }
    }

    override suspend fun addTrack(track: Track) {
        if (getStatusTrack(track.previewUrl) == -1){
            tracksDbRepository.insertTrack(track)
        }
    }

    override fun getTracks(): Flow<List<Track>> {
        return tracksDbRepository.getTracks()
    }

    override fun getTracks(ids: List<Int>): Flow<List<Track>> {
        return tracksDbRepository.getTracks(ids)
    }

    override fun getLikeTrack(like: Boolean): Flow<List<Track>> {
        return tracksDbRepository.getLikeTrack(like)
    }

    override suspend fun getIdTrack(previewUrl: String): Int {
        return tracksDbRepository.getIdTrack(previewUrl)
    }

    override suspend fun getStatusTrack(previewUrl: String): Int {
        return when (tracksDbRepository.getStatusTrack(previewUrl)) {
            true -> 1
            false -> 0
            else -> -1
        }
    }

    override suspend fun setStatusTrack(previewUrl: String, like: Boolean) {
        tracksDbRepository.setStatusTrack(previewUrl, like)
    }
}