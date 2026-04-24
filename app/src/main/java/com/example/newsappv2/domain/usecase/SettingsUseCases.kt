package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.util.ThemeMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeModeUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    operator fun invoke(): Flow<ThemeMode> = userPreferences.themeMode
}

class SaveThemeModeUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    suspend operator fun invoke(mode: ThemeMode) {
        userPreferences.saveThemeMode(mode)
    }
}

class GetUiLanguageUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    operator fun invoke(): Flow<String> = userPreferences.uiLanguage
}

class GetTargetLanguageUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    operator fun invoke(): Flow<String> = userPreferences.targetLanguage
}

class SaveTargetLanguageUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    suspend operator fun invoke(languageCode: String) {
        userPreferences.saveTargetLanguage(languageCode)
    }
}

class SettingsUseCases @Inject constructor(
    val getThemeMode: GetThemeModeUseCase,
    val saveThemeMode: SaveThemeModeUseCase,
    val getUiLanguage: GetUiLanguageUseCase,
    val getTargetLanguage: GetTargetLanguageUseCase,
    val saveTargetLanguage: SaveTargetLanguageUseCase
)