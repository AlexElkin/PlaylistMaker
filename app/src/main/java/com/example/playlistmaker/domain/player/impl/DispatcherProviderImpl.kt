package com.example.playlistmaker.domain.player.impl


import com.example.playlistmaker.domain.player.api.DispatcherProvider
import kotlinx.coroutines.Dispatchers

class DispatcherProviderImpl : DispatcherProvider {
    override val io = Dispatchers.IO
    override val default = Dispatchers.Default
    override val main = Dispatchers.Main
}