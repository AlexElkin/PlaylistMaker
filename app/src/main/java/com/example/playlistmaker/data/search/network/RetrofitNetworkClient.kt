package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.Response
import com.example.playlistmaker.data.search.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
//Реализация контракта NetworkClient через Retrofit
class RetrofitNetworkClient : NetworkClient {

    private val url = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(ApiService::class.java)

    override suspend fun doRequest(dto: Any): Response {
        return try {
            if (dto is TrackSearchRequest) {
                imdbService.getTrack(dto.expression).apply {
                    resultCode = 200
                }
            } else {
                Response().apply { resultCode = 400 }
            }
        } catch (e: IOException) {
            Response().apply {
                resultCode = -1
                errorMsg = "No internet connection"
            }
        } catch (e: Exception) {
            Response().apply {
                resultCode = 500
                errorMsg = e.message ?: "Unknown error"
            }

            throw e
        }
    }
}