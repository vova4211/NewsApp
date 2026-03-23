package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappv2.domain.usecase.GetIsFirstLaunchUseCase // ТІЛЬКИ USE CASE
import com.example.newsappv2.domain.usecase.GetThemeModeUseCase
import com.example.newsappv2.util.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getThemeModeUseCase: GetThemeModeUseCase,
    getIsFirstLaunchUseCase: GetIsFirstLaunchUseCase
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = getThemeModeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    val isFirstLaunch: StateFlow<Boolean?> = getIsFirstLaunchUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}