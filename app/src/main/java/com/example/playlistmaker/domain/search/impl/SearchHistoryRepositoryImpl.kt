package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.KEY_SEARCH_HISTORY
import com.example.playlistmaker.data.MAX_HISTORY_ITEMS
import com.example.playlistmaker.data.SharedPreferences
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.search.api.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistoryRepositoryImpl (private val sharedPreferences: SharedPreferences,
                                   private val gson: Gson = Gson()): SearchHistoryRepository {
    override suspend fun getSearchHistory(): List<Track>? {
        var json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null) ?: return null
        return gson.fromJson(json, object : TypeToken<List<Track>>() {}.type)
    }

    override suspend fun addToSearchHistory(track: Track) {
        val currentHistory = getSearchHistory()?.toMutableList()?:mutableListOf()

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

    override suspend fun clearSearchHistory() {
        sharedPreferences.remove(KEY_SEARCH_HISTORY)
    }

    private fun saveHistory(tracks: List<Track>) {
        sharedPreferences.saveString(KEY_SEARCH_HISTORY, gson.toJson(tracks))
        }


}




