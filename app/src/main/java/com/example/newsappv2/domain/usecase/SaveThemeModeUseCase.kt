package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.util.ThemeMode
import javax.inject.Inject

class SaveThemeModeUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    suspend operator fun invoke(mode: ThemeMode) {
        userPreferences.saveThemeMode(mode)
    }
}