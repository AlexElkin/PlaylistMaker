package com.example.playlistmaker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

fun networkTest(context: Context):Boolean{
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        // Для более старых версий Android можно использовать альтернативные методы
        false
    }
}