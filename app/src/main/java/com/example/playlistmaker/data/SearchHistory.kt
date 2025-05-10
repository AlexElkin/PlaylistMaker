package com.example.playlistmaker.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.data.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson = Gson()
) {


    fun getHistory(): List<TrackDto>? {
        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null) ?: return null
        return gson.fromJson(json, object : TypeToken<List<TrackDto>>() {}.type)
    }

    fun addTrack(track: TrackDto) {
        val currentHistory = getHistory()?.toMutableList() ?: mutableListOf()

        currentHistory.removeAll {
            it.trackName == track.trackName &&
                    it.artistName == track.artistName &&
                    it.previewUrl == track.previewUrl
        }

        if (currentHistory.size >= MAX_HISTORY_ITEMS) {
            currentHistory.removeAt(0)
        }

        currentHistory.add(track)
        saveHistory(currentHistory)
    }

    fun clearHistory() {
        sharedPreferences.edit { remove(KEY_SEARCH_HISTORY) }
    }

    private fun saveHistory(tracks: List<TrackDto>) {
        sharedPreferences.edit {
            putString(KEY_SEARCH_HISTORY, gson.toJson(tracks))
        }
    }
}