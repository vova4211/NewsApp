package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.domain.usecase.ClearUnsavedArticlesUseCase
import com.example.newsappv2.domain.usecase.GetThemeModeUseCase
import com.example.newsappv2.domain.usecase.GetUserAvatarUriUseCase
import com.example.newsappv2.domain.usecase.GetUserNameUseCase
import com.example.newsappv2.domain.usecase.SaveTargetLanguageUseCase
import com.example.newsappv2.domain.usecase.SaveThemeModeUseCase
import com.example.newsappv2.domain.usecase.UpdateProfileUseCase
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
    private val saveTargetLanguageUseCase: SaveTargetLanguageUseCase,
    private val clearUnsavedArticlesUseCase: ClearUnsavedArticlesUseCase,
    private val getThemeModeUseCase: GetThemeModeUseCase,
    private val saveThemeModeUseCase: SaveThemeModeUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val getUserAvatarUriUseCase: GetUserAvatarUriUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
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

    val currentThemeMode: StateFlow<ThemeMode> = getThemeModeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    val userName: StateFlow<String> = getUserNameUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val userAvatarUri: StateFlow<String> = getUserAvatarUriUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun setTargetLanguage(languageCode: String) {
        viewModelScope.launch {
            saveTargetLanguageUseCase(languageCode)
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
            saveThemeModeUseCase(mode)
        }
    }

    fun updateProfile(name: String, avatarUri: String) {
        viewModelScope.launch {
            updateProfileUseCase(name, avatarUri)
        }
    }
}