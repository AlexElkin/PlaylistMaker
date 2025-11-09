package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.settings.SettingRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingRepository: SettingRepository) : ViewModel() {

    private val _themeState = MutableStateFlow(settingRepository.getTheme())
    val themeState: StateFlow<Boolean> = _themeState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _toastMessage = Channel<String>()
    val toastMessage = _toastMessage.receiveAsFlow()

    fun onShareAppClicked() = viewModelScope.launch {
        _navigationEvent.send(NavigationEvent.ShareApp)
    }

    fun onSupportClicked() = viewModelScope.launch {
        _navigationEvent.send(NavigationEvent.Support)
    }

    fun onUserAgreementClicked() = viewModelScope.launch {
        _navigationEvent.send(NavigationEvent.UserAgreement)
    }

    fun onThemeSwitchChanged(isChecked: Boolean) {
        settingRepository.setTheme(isChecked)
        _themeState.value = isChecked
    }

    fun showToast(message: String) = viewModelScope.launch {
        _toastMessage.send(message)
    }

    sealed class NavigationEvent {
        object ShareApp : NavigationEvent()
        object Support : NavigationEvent()
        object UserAgreement : NavigationEvent()
    }
}