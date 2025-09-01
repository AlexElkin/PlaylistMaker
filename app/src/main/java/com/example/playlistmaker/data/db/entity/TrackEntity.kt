package com.example.playlistmaker.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "tracks_table")
@Parcelize
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "track_name")
    val trackName: String,
    @ColumnInfo(name = "artist_name")
    val artistName: String,
    @ColumnInfo(name = "collection_name")
    val collectionName: String,
    @ColumnInfo(name = "track_time_millis")
    val trackTimeMillis: Long,
    @ColumnInfo(name = "release_date")
    val releaseDate: String,
    @ColumnInfo(name = "primary_genre_name")
    val primaryGenreName: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "artwork_url_100")
    val artworkUrl100: String,
    @ColumnInfo(name = "preview_url")
    val previewUrl: String,
    @ColumnInfo(name = "like")
    val like: Boolean
) : Parcelable