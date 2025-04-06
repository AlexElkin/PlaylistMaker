package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.data_classes.Track
import com.google.gson.Gson
import androidx.core.content.edit
import com.google.gson.reflect.TypeToken

class SearchHistory(sharedPreferences: SharedPreferences) {

    val shared = sharedPreferences

    fun read(): MutableList<Track>? {
        val json = shared.getString(LIST_TRACKS, null) ?: return null
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun write(tracks: MutableList<Track>) {
        val json = Gson().toJson(tracks)
        shared.edit() {
            putString(LIST_TRACKS, json)
        }
    }

    fun removeHistory() {
        val historyTracks = read()
        if (historyTracks != null) {
            historyTracks.clear()
            write(historyTracks)
        }
    }

    fun addTrack(track: Track) {
        val historyTracks = read()
        if (historyTracks != null) {
            if (historyTracks.contains(track)) {
                historyTracks.remove(track)
            }
            if (historyTracks.size >= 10) {
                historyTracks.removeAt(0)
            }
            historyTracks.add(track)
            write(historyTracks)
        } else {
            write(mutableListOf<Track>(track))
        }
    }
}