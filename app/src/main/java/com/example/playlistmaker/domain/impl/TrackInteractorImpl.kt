package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.dto.Track
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import kotlinx.coroutines.withContext
//реализация контракта TrackInteractor по выполнению бизнес-логики (получение вводимой строки,возврат списков треков)
class TrackInteractorImpl(
    private val repository: TrackRepository,
    private val dispatchers: DispatcherProviderImpl
) : TrackInteractor {


    override suspend fun searchTrack(expression: String): List<Track> {
        return withContext(dispatchers.io) {
            repository.searchTrack(expression)
        }
    }
}
