package com.example.playlistmaker.ui.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(private val settingRepository: SettingRepository) : ViewModel() {


    private val _themeState = MutableStateFlow(settingRepository.getTheme())
    val themeState: StateFlow<Boolean> = _themeState.asStateFlow()

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    fun onBackClicked() = _navigationEvent.postValue(NavigationEvent.Back)
    fun onShareAppClicked() = _navigationEvent.postValue(NavigationEvent.ShareApp)
    fun onSupportClicked() = _navigationEvent.postValue(NavigationEvent.Support)
    fun onUserAgreementClicked() = _navigationEvent.postValue(NavigationEvent.UserAgreement)

    fun onThemeSwitchChanged(isChecked: Boolean) {//если изменился переключатель
        if (themeState.value != isChecked) {//если старые данные не равны положению переключателя
            settingRepository.setTheme(isChecked)//сохранить тему
            _themeState.value = isChecked//изменить данные на которые подписан

        }
    }

    fun showToast(message: String) {
        _toastMessage.postValue(message)
    }

    sealed class NavigationEvent {
        object Back : NavigationEvent()
        object ShareApp : NavigationEvent()
        object Support : NavigationEvent()
        object UserAgreement : NavigationEvent()
    }
}