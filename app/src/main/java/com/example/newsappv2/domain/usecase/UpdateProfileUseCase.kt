package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    suspend operator fun invoke(name: String, avatarUri: String) {
        userPreferences.updateProfile(name, avatarUri)
    }
}