package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.domain.usecase.GetCategoryNewsUseCase
import com.example.newsappv2.domain.usecase.GetSavedCategoryUseCase
import com.example.newsappv2.domain.usecase.SaveCategoryUseCase
import com.example.newsappv2.util.Category
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoryNewsUseCase: GetCategoryNewsUseCase,
    private val getSavedCategoryUseCase: GetSavedCategoryUseCase,
    private val saveCategoryUseCase: SaveCategoryUseCase,
) : ViewModel() {

    private val _selectCategory = MutableStateFlow(Category.BUSINESS)
    val selectCategory: StateFlow<Category> = _selectCategory.asStateFlow()


    init {
        viewModelScope.launch {
            getSavedCategoryUseCase().collectLatest { category ->
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
                getCategoryNewsUseCase(category.categoryName.lowercase())
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
            saveCategoryUseCase(category)
        }
    }
}