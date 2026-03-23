package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    operator fun invoke(): Flow<String> {
        return userPreferences.userName
    }
}