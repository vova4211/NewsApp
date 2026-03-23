package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.util.ThemeMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeModeUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    operator fun invoke(): Flow<ThemeMode> {
        return userPreferences.themeMode
    }
}