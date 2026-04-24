package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.domain.usecase.NewsUseCases
import com.example.newsappv2.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SavedArticlesViewModel @Inject constructor(
    private val newsUseCases: NewsUseCases,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val savedArticles: StateFlow<List<Article>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                newsUseCases.getSavedArticles()
            } else {
                newsUseCases.searchSavedArticles(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleBookmark(url: String, isSaved: Boolean) {
        viewModelScope.launch {
            toggleBookmarkUseCase(url = url, isSaved = isSaved)
        }
    }
}