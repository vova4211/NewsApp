package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappv2.domain.usecase.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    fun saveProfileAndFinishOnboarding(name: String, avatarUri: String) {
        viewModelScope.launch {
            profileUseCases.completeOnboarding(name, avatarUri)
        }
    }
}