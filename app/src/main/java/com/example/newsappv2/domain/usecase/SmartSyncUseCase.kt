package com.example.newsappv2.domain.usecase

import android.util.Log
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.data.mlkit.OfflineTranslator
import com.example.newsappv2.data.remote.HtmlParser
import com.example.newsappv2.data.remote.translation.CloudTranslator
import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.util.Constants.DEFAULT_QUERY
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SmartSyncUseCase @Inject constructor(
    private val repository: NewsRepository,
    private val htmlParser: HtmlParser,
    private val cloudTranslator: CloudTranslator,
    private val offlineTranslator: OfflineTranslator,
    private val userPreferences: UserPreferencesDataStore
) {
    private val TAG = "SmartSync"

    suspend operator fun invoke() {
        Log.d(TAG, "Починаємо фонову синхронізацію...")

        val articlesToSync = repository.getArticlesForSmartSync(DEFAULT_QUERY)

        if (articlesToSync.isEmpty()) {
            Log.d(TAG, "Немає нових статей для синхронізації.")
            return
        }

        val targetLangCode = userPreferences.targetLanguage.firstOrNull() ?: "en"
        offlineTranslator.downloadModelIfNeeded(targetLangCode)

        for (article in articlesToSync) {
            try {
                Log.d(TAG, "Завантажуємо повний текст для: ${article.url}")
                val parseResult = htmlParser.extractArticleText(article.url)
                val fullText = parseResult.getOrNull()

                if (!fullText.isNullOrBlank()) {
                    repository.updateArticleFullText(article.url, fullText)

                    val cloudResult = cloudTranslator.translate(fullText, targetLangCode)
                    val translatedText = if (cloudResult.isSuccess) {
                        cloudResult.getOrNull()
                    } else {
                        Log.e(TAG, "Cloud API впав під час синхронізації. Використовуємо ML Kit.")
                        offlineTranslator.translate(fullText, targetLangCode).getOrNull()
                    } ?: fullText

                    val titleToSave = article.translatedTitle ?: article.title ?: ""
                    repository.updateArticleTranslation(article.url, translatedText, titleToSave)

                    Log.d(TAG, "Успішно синхронізовано: ${article.url}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Помилка синхронізації для статті: ${article.url}", e)
            }
        }

        Log.d(TAG, "Фонова синхронізація завершена!")
    }
}