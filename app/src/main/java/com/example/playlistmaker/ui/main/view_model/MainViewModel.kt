package com.example.playlistmaker.ui.main.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _navigationEvent = MutableLiveData<NavigationDestination>()
    val navigationEvent: LiveData<NavigationDestination> = _navigationEvent

    fun onSearchClicked() {
        _navigationEvent.value = NavigationDestination.SEARCH
    }

    fun onLibraryClicked() {
        _navigationEvent.value = NavigationDestination.LIBRARY
    }

    fun onSettingsClicked() {
        _navigationEvent.value = NavigationDestination.SETTINGS
    }
    enum class NavigationDestination{
        SEARCH, LIBRARY, SETTINGS
    }
}