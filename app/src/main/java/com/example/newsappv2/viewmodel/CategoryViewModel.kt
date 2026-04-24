package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.domain.usecase.NewsUseCases
import com.example.newsappv2.domain.usecase.PreferencesUseCases
import com.example.newsappv2.domain.usecase.SettingsUseCases
import com.example.newsappv2.domain.usecase.ToggleBookmarkUseCase
import com.example.newsappv2.util.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val newsUseCases: NewsUseCases,
    private val preferencesUseCases: PreferencesUseCases,
    private val settingsUseCases: SettingsUseCases,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _selectCategory = MutableStateFlow(Category.BUSINESS)
    val selectCategory: StateFlow<Category> = _selectCategory.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesUseCases.getSavedCategory().collectLatest { category ->
                _selectCategory.value = category
            }
        }
    }

    val targetLanguage: StateFlow<String> = settingsUseCases.getTargetLanguage()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val newsCategoryPagingFlow: StateFlow<PagingData<Article>> =
        _selectCategory
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { category ->
                newsUseCases.getCategoryNews(category.apiValue)
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
            preferencesUseCases.saveCategory(category)
        }
    }

    fun toggleBookmark(url: String, isSaved: Boolean) {
        viewModelScope.launch {
            toggleBookmarkUseCase(url = url, isSaved = isSaved)
        }
    }
}