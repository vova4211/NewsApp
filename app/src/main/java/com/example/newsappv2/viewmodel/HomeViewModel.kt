package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.domain.usecase.GetSavedQueryUseCase
import com.example.newsappv2.domain.usecase.GetSearchNewsUseCase
import com.example.newsappv2.domain.usecase.SaveQueryUseCase
import com.example.newsappv2.util.Constants.DEFAULT_QUERY
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class HomeViewModel @Inject constructor(
    private val getSearchNewsUseCase: GetSearchNewsUseCase,
    private val getSavedQueryUseCase: GetSavedQueryUseCase,
    private val saveQueryUseCase: SaveQueryUseCase,
) : ViewModel() {

    private val _searchQuery  = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val defaultQuery: String  = DEFAULT_QUERY

    private val _lastSearchQuery = MutableStateFlow("")
    val lastSearchQuery: StateFlow<String> = _lastSearchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            getSavedQueryUseCase().collectLatest { query ->
                _lastSearchQuery.value = query
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeNewsPagingFlow: StateFlow<PagingData<Article>> =
        _searchQuery
            .debounce(1200)
            .distinctUntilChanged()
            .map { query -> if(query.isBlank()) defaultQuery else query }
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

    fun persistLastSearchQuery(searchQuery : String) {
        viewModelScope.launch {
            saveQueryUseCase(searchQuery)
        }
    }
}