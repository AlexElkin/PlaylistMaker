package com.example.playlistmaker.data.library

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlists(val title: String, val picture:String, var countTracks: Int = 0) : Parcelable