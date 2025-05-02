package com.example.playlistmaker

import android.os.Handler
import android.os.Looper

class Debouncer(private val delayMillis: Long) {
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    fun debounce(action: () -> Unit) {
        runnable?.let { handler.removeCallbacks(it) }
        runnable = Runnable {
            action()
            runnable = null
        }
        handler.postDelayed(runnable!!, delayMillis)
    }

}