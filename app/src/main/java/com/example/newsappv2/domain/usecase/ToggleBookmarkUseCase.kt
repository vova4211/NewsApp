package com.example.newsappv2.domain.usecase

import android.util.Log
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.data.mlkit.OfflineTranslator
import com.example.newsappv2.data.remote.HtmlParser
import com.example.newsappv2.data.remote.translation.CloudTranslator
import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.util.NetworkMonitor
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: NewsRepository,
    private val htmlParser: HtmlParser,
    private val cloudTranslator: CloudTranslator,
    private val offlineTranslator: OfflineTranslator,
    private val networkMonitor: NetworkMonitor,
    private val userPreferences: UserPreferencesDataStore
) {
    private val TAG = "ToggleBookmark"

    suspend operator fun invoke(url: String, isSaved: Boolean) {
        repository.updateSavedStatus(url, isSaved)

        if (!isSaved) {
            Log.d(TAG, "Статтю видалено із закладок: $url")
            return
        }

        try {
            val article = repository.getArticleByUrl(url).firstOrNull()
            if (article?.fullText != null && article.translatedText != null) {
                Log.d(TAG, "Стаття вже завантажена та перекладена, пропускаємо парсинг.")
                return
            }

            Log.d(TAG, "Починаємо парсинг Jsoup для: $url")
            val parseResult = htmlParser.extractArticleText(url)
            val fullText = parseResult.getOrNull()

            if (!fullText.isNullOrBlank()) {
                repository.updateArticleFullText(url, fullText)

                val targetLangCode = userPreferences.targetLanguage.firstOrNull() ?: "en"
                val hasInternet = networkMonitor.isOnline()

                Log.d(TAG, "Перекладаємо текст. Інтернет: $hasInternet, Мова: $targetLangCode")
                offlineTranslator.downloadModelIfNeeded(targetLangCode)

                val translatedText = if (hasInternet) {
                    val cloudResult = cloudTranslator.translate(fullText, targetLangCode)
                    if (cloudResult.isSuccess) {
                        cloudResult.getOrNull()
                    } else {
                        Log.e(TAG, "Cloud API впав, переходимо на ML Kit")
                        offlineTranslator.translate(fullText, targetLangCode).getOrNull()
                    }
                } else {
                    Log.d(TAG, "Немає інтернету, використовуємо ML Kit")
                    offlineTranslator.translate(fullText, targetLangCode).getOrNull()
                } ?: fullText

                val titleToSave = article?.translatedTitle ?: article?.title ?: ""
                repository.updateArticleTranslation(url, translatedText, titleToSave)

                Log.d(TAG, "Успішно збережено і перекладено для офлайн-читання!")
            } else {
                Log.e(TAG, "Парсер не зміг витягнути текст")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Помилка при фоновому збереженні статті", e)
        }
    }
}