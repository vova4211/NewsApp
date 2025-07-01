package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.data.model.Article
import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.util.Category
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

class CategoryViewModel(
    private val repository: NewsRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _selectCategory = MutableStateFlow(Category.BUSINESS)
    val selectCategory: StateFlow<Category> = _selectCategory.asStateFlow()


    init {
        viewModelScope.launch {
            userPreferencesDataStore.selectCategory.collectLatest { category ->
                _selectCategory.value = category
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val newsCategoryPagingFlow: StateFlow<PagingData<Article>> =
        _selectCategory
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { category ->
                repository.getCategoryNewsPager(category.categoryName.lowercase())
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
    fun onCategorySelected(category: Category) {
        _selectCategory.value = category

    }

    fun persistLastSelectedCategory(category: Category) {
        viewModelScope.launch {
            userPreferencesDataStore.saveSelectedCategory(category)
        }
    }
}