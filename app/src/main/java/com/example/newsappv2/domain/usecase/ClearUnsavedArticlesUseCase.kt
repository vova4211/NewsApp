package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.repository.NewsRepository
import javax.inject.Inject

class ClearUnsavedArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke() {
        newsRepository.clearUnreadUnsavedArticles()

        newsRepository.resetTranslationsForUnsaved()
    }
}