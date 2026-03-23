package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.repository.NewsRepository
import javax.inject.Inject

class ClearUnsavedArticlesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke() {
        repository.clearUnsavedArticles()
    }
}