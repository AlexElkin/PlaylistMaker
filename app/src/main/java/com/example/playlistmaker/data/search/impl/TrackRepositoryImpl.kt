package com.example.playlistmaker.data.search.impl


import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.api.TrackRepository
import com.example.playlistmaker.data.search.TrackResponse
import com.example.playlistmaker.data.search.TrackSearchRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
//Реализация репозитория для работы с сетью, только получает данные
class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override suspend fun searchTrack(expression: String): Flow<List<Track>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            200 -> {val tracks = ( response as TrackResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.collectionName,
                    it.trackTimeMillis,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.artworkUrl100,
                    it.previewUrl
                )
            }
            emit(tracks)}

            -1 -> throw NoInternetException()
            else -> throw Exception("Server error: ${response.errorMsg}")
        }
    }
}

class NoInternetException : IOException("No internet connection")