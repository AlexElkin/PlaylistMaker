package com.example.playlistmaker

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.DispatcherProviderImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getNetworkClient(): NetworkClient {
        return RetrofitNetworkClient()
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(getNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(), DispatcherProviderImpl())
    }
}