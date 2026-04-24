package com.example.newsappv2.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.util.toDomainArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoryNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(category: String): Flow<PagingData<Article>> {
        return repository.getCategoryNewsPager(category).map { pagingData ->
            pagingData.map { it.toDomainArticle() }
        }
    }
}

class GetSearchNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Article>> {
        return repository.getSearchNewsPager(query).map { pagingData ->
            pagingData.map { it.toDomainArticle() }
        }
    }
}

class GetSavedArticlesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(): Flow<List<Article>> {
        return repository.getSavedArticles().map { entities ->
            entities.map { it.toDomainArticle() }
        }
    }
}

class SearchSavedArticlesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(searchQuery: String): Flow<List<Article>> {
        return repository.searchSavedArticles(searchQuery).map { entities ->
            entities.map { it.toDomainArticle() }
        }
    }
}

class NewsUseCases @Inject constructor(
    val getCategoryNews: GetCategoryNewsUseCase,
    val getSearchNews: GetSearchNewsUseCase,
    val getSavedArticles: GetSavedArticlesUseCase,
    val searchSavedArticles: SearchSavedArticlesUseCase
)