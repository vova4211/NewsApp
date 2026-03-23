package com.example.newsappv2.domain.usecase

import android.util.Log
import com.example.newsappv2.data.local.dao.SearchHistoryDao
import com.example.newsappv2.data.local.db.entities.SearchQueryEntity
import com.example.newsappv2.data.remote.translation.CloudTranslator
import javax.inject.Inject

class ProcessSearchQueryUseCase @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao,
    private val cloudTranslator: CloudTranslator
) {
    private val TAG = "SmartSearch"

    suspend operator fun invoke(rawQuery: String): String {
        if (rawQuery.isBlank()) return ""

        val entity = SearchQueryEntity(
            searchQuery = rawQuery.trim(),
            timestamp = System.currentTimeMillis()
        )
        try {
            searchHistoryDao.insertAndCleanup(entity)
            Log.d(TAG, "Запит '$rawQuery' збережено в історію")
        } catch (e: Exception) {
            Log.e(TAG, "Помилка збереження історії", e)
        }

        Log.d(TAG, "Перекладаємо запит '$rawQuery' на англійську...")
        val translatedResult = cloudTranslator.translate(text = rawQuery, targetLanguage = "en")

        return if (translatedResult.isSuccess) {
            val translatedQuery = translatedResult.getOrNull() ?: rawQuery
            Log.d(TAG, "Перекладений запит: '$translatedQuery'")
            translatedQuery
        } else {
            Log.e(TAG, "Помилка перекладу запиту, використовуємо оригінал")
            rawQuery
        }
    }
}