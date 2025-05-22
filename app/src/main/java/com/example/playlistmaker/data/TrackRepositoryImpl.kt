package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.Track
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.domain.api.TrackRepository
import java.io.IOException
import kotlin.String
//Реализация репозитория для работы с сетью, только получает данные
class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override suspend fun searchTrack(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when (response.resultCode) {
            200 -> (response as TrackResponse).results.map {
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

            -1 -> throw NoInternetException()
            else -> throw Exception("Server error: ${response.errorMsg}")
        }
    }
}

class NoInternetException : IOException("No internet connection")