package com.example.playlistmaker.data_classes
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val trackTimeMillis: Long,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val artworkUrl100: String,
    val previewUrl: String
) : Parcelable


data class TrackResponse(
    val results: List<Track>
)