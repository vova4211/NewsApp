package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import javax.inject.Inject

class SaveTargetLanguageUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    suspend operator fun invoke(languageCode: String) {
        userPreferences.saveTargetLanguage(languageCode)
    }
}