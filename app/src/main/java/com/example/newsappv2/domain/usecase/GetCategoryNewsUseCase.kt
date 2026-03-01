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
    operator fun invoke(category: String) : Flow<PagingData<Article>> {
        return  repository.getCategoryNewsPager(category).map { pagingData ->
            pagingData.map { articleEntity ->
                articleEntity.toDomainArticle()
            }
        }
    }
}