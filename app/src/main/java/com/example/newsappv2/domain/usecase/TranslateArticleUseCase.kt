package com.example.newsappv2.domain.usecase

import android.util.Log
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.data.mlkit.LanguageIdentifier
import com.example.newsappv2.data.mlkit.OfflineTranslator
import com.example.newsappv2.data.remote.translation.CloudTranslator
import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.util.NetworkMonitor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class TranslateArticleUseCase @Inject constructor(
    private val offlineTranslator: OfflineTranslator,
    private val cloudTranslator: CloudTranslator,
    private val languageIdentifier: LanguageIdentifier,
    private val networkMonitor: NetworkMonitor,
    private val repository: NewsRepository,
    private val userPreferences: UserPreferencesDataStore
) {
    private val TAG = "TranslationArchitecture"

    suspend operator fun invoke(url: String): Result<Unit> {
        return try {
            val articleEntity = repository.getArticleByUrl(url).firstOrNull()
            val textToTranslate = articleEntity?.fullText ?: articleEntity?.description ?: ""
            val titleToTranslate = articleEntity?.title ?: "Без заголовка"

            if (textToTranslate.isBlank()) {
                return Result.failure(Exception("Немає тексту для перекладу."))
            }

            val targetLangCode = userPreferences.targetLanguage.first()
            Log.d(TAG, "Користувач обрав цільову мову: $targetLangCode")

            val detectedLang = languageIdentifier.identify(textToTranslate.take(100))
            Log.d(TAG, "Detected Language: $detectedLang")
            if (detectedLang == targetLangCode) {
                Log.d(TAG, "Текст вже потрібною мовою, зберігаємо як переклад.")
                repository.updateArticleTranslation(url, textToTranslate, titleToTranslate)
                return Result.success(Unit)
            }

            Log.d(TAG, "Ініціалізація ML Kit для мови $targetLangCode...")
            offlineTranslator.downloadModelIfNeeded(targetLangCode)

            val hasInternet = networkMonitor.isOnline()
            Log.d(TAG, "Internet status: $hasInternet")

            suspend fun smartTranslate(text: String): String {
                if (text.isBlank()) return ""

                if (!hasInternet) {
                    Log.d(TAG, "Немає мережі -> ML Kit")
                    return offlineTranslator.translate(text, targetLangCode).getOrNull() ?: text
                }

                val cloudResult = cloudTranslator.translate(text, targetLangCode)
                if (cloudResult.isSuccess) {
                    return cloudResult.getOrNull() ?: text
                }

                Log.e(TAG, "Cloud failed -> ML Kit fallback")
                return offlineTranslator.translate(text, targetLangCode).getOrNull() ?: text
            }

            Log.d(TAG, "Починаємо переклад...")
            val translatedTitle = smartTranslate(titleToTranslate)
            Log.d(TAG, "Translated Title: $translatedTitle")

            val paragraphs = textToTranslate.split("\n\n")
            val translatedParagraphs = mutableListOf<String>()

            for ((index, paragraph) in paragraphs.withIndex()) {
                Log.d(TAG, "Переклад абзацу ${index + 1}/${paragraphs.size}")
                translatedParagraphs.add(smartTranslate(paragraph))
            }

            val finalTranslatedText = translatedParagraphs.joinToString("\n\n")

            Log.d(TAG, "Зберігаємо переклад в БД для URL: $url")
            repository.updateArticleTranslation(url, finalTranslatedText, translatedTitle)
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Fatal Translation Error", e)
            Result.failure(e)
        }
    }
}