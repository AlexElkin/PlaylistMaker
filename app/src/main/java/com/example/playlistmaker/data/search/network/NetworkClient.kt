package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.Response

// контракт на асинхронный запрос
fun interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}