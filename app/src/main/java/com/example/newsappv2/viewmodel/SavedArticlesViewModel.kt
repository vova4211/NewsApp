package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.domain.usecase.GetSavedArticlesUseCase
import com.example.newsappv2.domain.usecase.SearchSavedArticlesUseCase
import com.example.newsappv2.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SavedArticlesViewModel @Inject constructor(
    private val getSavedArticlesUseCase: GetSavedArticlesUseCase,
    private val searchSavedArticlesUseCase: SearchSavedArticlesUseCase, // ДОДАЛИ
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val savedArticles: StateFlow<List<Article>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getSavedArticlesUseCase()
            } else {
                searchSavedArticlesUseCase(query)
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