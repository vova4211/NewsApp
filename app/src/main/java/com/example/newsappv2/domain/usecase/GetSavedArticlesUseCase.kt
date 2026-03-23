package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.util.toDomainArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSavedArticlesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(): Flow<List<Article>> {
        return repository.getSavedArticles().map { entities ->
            entities.map { it.toDomainArticle() }
        }
    }
}