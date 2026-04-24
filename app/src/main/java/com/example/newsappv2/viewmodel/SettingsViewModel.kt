package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.domain.usecase.ClearUnsavedArticlesUseCase
import com.example.newsappv2.domain.usecase.ProfileUseCases
import com.example.newsappv2.domain.usecase.SettingsUseCases
import com.example.newsappv2.util.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferencesDataStore,
    private val settingsUseCases: SettingsUseCases,
    private val profileUseCases: ProfileUseCases,
    private val clearUnsavedArticlesUseCase: ClearUnsavedArticlesUseCase
) : ViewModel() {

    val availableLanguages = mapOf(
        "Українська" to "uk",
        "English" to "en",
        "Polski" to "pl",
        "Español" to "es",
        "Deutsch" to "de",
        "Français" to "fr"
    )

    val currentTargetLanguageCode: StateFlow<String> = userPreferences.targetLanguage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "uk"
        )

    val currentUiLanguageCode: StateFlow<String> = userPreferences.uiLanguage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "uk"
        )

    val currentThemeMode: StateFlow<ThemeMode> = settingsUseCases.getThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    val userName: StateFlow<String> = profileUseCases.getUserName()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val userAvatarUri: StateFlow<String> = profileUseCases.getUserAvatarUri()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun setTargetLanguage(languageCode: String) {
        viewModelScope.launch {
            settingsUseCases.saveTargetLanguage(languageCode)
            clearUnsavedArticlesUseCase()
        }
    }

    fun setUiLanguage(languageCode: String) {
        viewModelScope.launch {
            userPreferences.saveUiLanguage(languageCode)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsUseCases.saveThemeMode(mode)
        }
    }

    fun updateProfile(name: String, avatarUri: String) {
        viewModelScope.launch {
            profileUseCases.updateProfile(name, avatarUri)
        }
    }
}