package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    suspend operator fun invoke(name: String, avatarUri: String) {
        userPreferences.completeOnboarding(name, avatarUri)
    }
}

class UpdateProfileUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    suspend operator fun invoke(name: String, avatarUri: String) {
        userPreferences.updateProfile(name, avatarUri)
    }
}

class GetUserNameUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    operator fun invoke(): Flow<String> = userPreferences.userName
}

class GetUserAvatarUriUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    operator fun invoke(): Flow<String> = userPreferences.userAvatarUri
}

class GetIsFirstLaunchUseCase @Inject constructor(
    private val userPreferences: UserPreferencesDataStore
) {
    operator fun invoke(): Flow<Boolean> = userPreferences.isFirstLaunch
}

class ProfileUseCases @Inject constructor(
    val completeOnboarding: CompleteOnboardingUseCase,
    val updateProfile: UpdateProfileUseCase,
    val getUserName: GetUserNameUseCase,
    val getUserAvatarUri: GetUserAvatarUriUseCase,
    val getIsFirstLaunch: GetIsFirstLaunchUseCase
)