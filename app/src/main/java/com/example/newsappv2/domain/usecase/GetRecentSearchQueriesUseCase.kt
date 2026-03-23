package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.local.dao.SearchHistoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRecentSearchQueriesUseCase @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) {
    operator fun invoke(): Flow<List<String>> {
        return searchHistoryDao.getRecentQueries().map { entities ->
            entities.map { it.searchQuery }
        }
    }
}