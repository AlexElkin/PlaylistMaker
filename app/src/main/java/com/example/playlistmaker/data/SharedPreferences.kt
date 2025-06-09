package com.example.playlistmaker.data

interface SharedPreferences {
    fun saveString(key: String, value: String)

    fun getString(key: String,defaultValue: String?): String?

    fun saveBoolean(key: String, value: Boolean)

    fun getBoolean(key: String,defaultValue: Boolean = false): Boolean

    fun saveInt(key: String, value: Int)

    fun getInt(key: String,defaultValue: Int = 0): Int

    fun remove(key: String)

    fun contains(key: String): Boolean
}