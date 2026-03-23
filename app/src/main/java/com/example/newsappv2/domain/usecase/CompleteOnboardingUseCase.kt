package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    suspend operator fun invoke(name: String, avatarUri: String) {
        userPreferences.completeOnboarding(name, avatarUri)
    }
}