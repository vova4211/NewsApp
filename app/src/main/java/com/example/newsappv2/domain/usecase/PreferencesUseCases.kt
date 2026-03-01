package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.util.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedQueryUseCase @Inject constructor(private val dataStore: UserPreferencesDataStore) {
    operator fun invoke(): Flow<String> = dataStore.searchQuery
}

class SaveQueryUseCase @Inject constructor(private val dataStore: UserPreferencesDataStore) {
    suspend operator fun invoke(query: String) = dataStore.saveLastQuery(query)
}

class GetSavedCategoryUseCase @Inject constructor(private val dataStore: UserPreferencesDataStore) {
    operator fun invoke(): Flow<Category> = dataStore.selectCategory
}

class SaveCategoryUseCase @Inject constructor(private val dataStore: UserPreferencesDataStore) {
    suspend operator fun invoke(category: Category) = dataStore.saveSelectedCategory(category)
}

