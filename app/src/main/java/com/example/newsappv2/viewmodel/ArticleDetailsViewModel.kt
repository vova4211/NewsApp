package com.example.newsappv2.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.domain.usecase.DownloadArticleFullTextUseCase
import com.example.newsappv2.domain.usecase.TranslateArticleUseCase
import com.example.newsappv2.util.NetworkMonitor
import com.example.newsappv2.util.toDomainArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class ArticleDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: NewsRepository,
    private val downloadArticleFullTextUseCase: DownloadArticleFullTextUseCase,
    private val translateArticleUseCase: TranslateArticleUseCase,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val encodedUrl: String = checkNotNull(savedStateHandle["encodedUrl"])
    val articleUrl: String = URLDecoder.decode(encodedUrl, "UTF-8")

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating = _isTranslating.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val article: StateFlow<Article?> = repository.getArticleByUrl(articleUrl)
        .map { it?.toDomainArticle() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    init {
        downloadFullTextIfNeeded()
    }

    private fun downloadFullTextIfNeeded() {
        viewModelScope.launch {
            if (!networkMonitor.isOnline()) {
                return@launch
            }

            _isLoading.value = true
            _error.value = null

            val result = downloadArticleFullTextUseCase(articleUrl)

            if (result.isFailure) {
                val currentArticle = article.value
                if (currentArticle?.fullText == null && currentArticle?.description.isNullOrBlank()) {
                    _error.value = "Немає підключення до мережі. Перевірте інтернет."
                }
            }
            _isLoading.value = false
        }
    }

    fun translateArticle() {
        viewModelScope.launch {
            _isTranslating.value = true
            _error.value = null

            val result = translateArticleUseCase(articleUrl)

            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Помилка перекладу. Перевірте підключення до мережі для першого завантаження моделі."
            }
            _isTranslating.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}