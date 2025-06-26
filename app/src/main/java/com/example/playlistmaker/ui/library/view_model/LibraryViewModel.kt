package com.example.playlistmaker.ui.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LibraryViewModel() : ViewModel() {

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    fun onBackClicked() = _navigationEvent.postValue(NavigationEvent.Back)

    sealed class NavigationEvent {
        object Back : NavigationEvent()
    }
}