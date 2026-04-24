package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappv2.domain.usecase.ProfileUseCases
import com.example.newsappv2.domain.usecase.SettingsUseCases
import com.example.newsappv2.util.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsUseCases: SettingsUseCases,
    profileUseCases: ProfileUseCases
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsUseCases.getThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    val isFirstLaunch: StateFlow<Boolean?> = profileUseCases.getIsFirstLaunch()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val uiLangCode: StateFlow<String> = settingsUseCases.getUiLanguage()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "uk"
        )
}