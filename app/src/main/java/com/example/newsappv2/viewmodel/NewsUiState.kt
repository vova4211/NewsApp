package com.example.newsappv2.viewmodel

import com.example.newsappv2.data.model.NewsResponse

sealed interface NewsUiState {
    data class Success(val news: NewsResponse) : NewsUiState
    object Loading : NewsUiState
    object Error : NewsUiState
}