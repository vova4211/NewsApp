package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.domain.usecase.GetRecentSearchQueriesUseCase
import com.example.newsappv2.domain.usecase.GetSearchNewsUseCase
import com.example.newsappv2.domain.usecase.ProcessSearchQueryUseCase
import com.example.newsappv2.domain.usecase.ToggleBookmarkUseCase // ДОДАЛИ ІМПОРТ
import com.example.newsappv2.util.Constants.DEFAULT_QUERY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class HomeViewModel @Inject constructor(
    private val getSearchNewsUseCase: GetSearchNewsUseCase,
    private val processSearchQueryUseCase: ProcessSearchQueryUseCase,
    private val getRecentSearchQueriesUseCase: GetRecentSearchQueriesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val defaultQuery: String = DEFAULT_QUERY

    val recentQueries: StateFlow<List<String>> = getRecentSearchQueriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _translatedQuery = MutableStateFlow(defaultQuery)

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeNewsPagingFlow: StateFlow<PagingData<Article>> =
        _translatedQuery
            .flatMapLatest { query ->
                getSearchNewsUseCase(query)
            }
            .cachedIn(viewModelScope)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PagingData.empty()
            )

    fun onSearchTextChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            if (query.isNotBlank()) {
                val translated = processSearchQueryUseCase(query)
                _translatedQuery.value = translated
            } else {
                _translatedQuery.value = defaultQuery
            }
        }
    }

    fun toggleBookmark(url: String, isSaved: Boolean) {
        viewModelScope.launch {
            toggleBookmarkUseCase(url = url, isSaved = isSaved)
        }
    }
}