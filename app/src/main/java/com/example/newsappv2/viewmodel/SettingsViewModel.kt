package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore // Залишаємо для читання StateFlow, це допускається
import com.example.newsappv2.domain.usecase.ClearUnsavedArticlesUseCase
import com.example.newsappv2.domain.usecase.SaveTargetLanguageUseCase
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
    private val clearUnsavedArticlesUseCase: ClearUnsavedArticlesUseCase
) : ViewModel() {

    val availableLanguages = mapOf(
        "English" to "en",
        "Українська" to "uk",
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

    fun setTargetLanguage(languageCode: String) {
        viewModelScope.launch {
            saveTargetLanguageUseCase(languageCode) // Або userPreferences.saveTargetLanguage(languageCode)
            clearUnsavedArticlesUseCase()
        }
    }

    fun setUiLanguage(languageCode: String) {
        viewModelScope.launch {
            userPreferences.saveUiLanguage(languageCode)
            val localeList = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }
}