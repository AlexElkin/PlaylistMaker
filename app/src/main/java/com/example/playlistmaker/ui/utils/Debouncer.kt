package com.example.playlistmaker.ui.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class Debouncer(
    private val delayMillis: Long,
    private val coroutineScope: CoroutineScope
) {
    private var debounceJob: Job? = null

    fun debounce(action: () -> Unit) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(delayMillis)
            action()
        }
    }
}