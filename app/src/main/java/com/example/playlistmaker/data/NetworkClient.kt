package com.example.playlistmaker.data
import com.example.playlistmaker.data.dto.Response
// контракт на асинхронный запрос
fun interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}