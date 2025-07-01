package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.data.model.Article
import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.util.Constants.DEFAULT_QUERY
import com.example.newsappv2.util.toArticle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class)
class HomeViewModel(
    private val repository: NewsRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _searchQuery  = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val defaultQuery: String  = DEFAULT_QUERY

    private val _lastSearchQuery = MutableStateFlow("")
    val lastSearchQuery: StateFlow<String> = _lastSearchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesDataStore.searchQuery.collectLatest { query ->
                _lastSearchQuery.value = query
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeNewsPagingFlow: StateFlow<PagingData<Article>> =
        _searchQuery
            .debounce(500)
            .distinctUntilChanged()
            .map { query ->
                if(query.isBlank()) defaultQuery else query
            }
            .flatMapLatest { query ->
                repository.getSearchNewsPager(query)
                    .map { pagingData ->
                        pagingData.map { it.toArticle() }
                    }
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

    fun persistLastSearchQuery(searchQuery : String) {
        viewModelScope.launch {
            userPreferencesDataStore.saveLastQuery(searchQuery)
        }
    }
}