package com.example.playlistmaker.data

import android.content.Context
import androidx.core.content.edit

class SharedPreferencesImpl(context: Context) : SharedPreferences {
    private val sharedPreferences =  context.getSharedPreferences(MY_SAVES, Context.MODE_PRIVATE)

    override fun saveString(key: String, value: String){
        sharedPreferences.edit() { putString(key, value) }
    }

    override fun getString(key: String, defaultValue: String?): String?{
        return sharedPreferences.getString(key,defaultValue)
    }

    override fun saveBoolean(key: String, value: Boolean){
        sharedPreferences.edit() { putBoolean(key, value) }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean{
        return sharedPreferences.getBoolean(key,defaultValue)
    }

    override fun saveInt(key: String, value: Int){
        sharedPreferences.edit() { putInt(key, value) }
    }

    override fun getInt(key: String, defaultValue: Int): Int{
        return sharedPreferences.getInt(key,defaultValue)
    }

    override fun remove(key: String){
        sharedPreferences.edit() { remove(key).apply() }
    }

    override fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}