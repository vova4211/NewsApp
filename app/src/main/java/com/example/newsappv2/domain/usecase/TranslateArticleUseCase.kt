package com.example.newsappv2.domain.usecase

import android.util.Log
import com.example.newsappv2.data.mlkit.LanguageIdentifier
import com.example.newsappv2.data.mlkit.OfflineTranslator
import com.example.newsappv2.data.remote.translation.CloudTranslator
import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.util.NetworkMonitor
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class TranslateArticleUseCase @Inject constructor(
    private val offlineTranslator: OfflineTranslator,
    private val cloudTranslator: CloudTranslator,
    private val languageIdentifier: LanguageIdentifier,
    private val networkMonitor: NetworkMonitor,
    private val repository: NewsRepository
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

            // 1. ПЕРЕВІРКА МОВИ: Беремо тільки перші 100 символів для швидкого аналізу
            val detectedLang = languageIdentifier.identify(textToTranslate.take(100))
            Log.d(TAG, "Detected Language: $detectedLang")
            if (detectedLang == "uk" || detectedLang == "ru") {
                Log.d(TAG, "Текст вже зрозумілий, зберігаємо як переклад.")
                repository.updateArticleTranslation(url, textToTranslate, titleToTranslate)
                return Result.success(Unit)
            }

            // 2. ІНІЦІАЛІЗАЦІЯ ЛОКАЛЬНОЇ МОДЕЛІ
            Log.d(TAG, "Ініціалізація ML Kit...")
            offlineTranslator.downloadModelIfNeeded()

            // 3. ВИБІР СТРАТЕГІЇ
            val hasInternet = networkMonitor.isOnline()
            Log.d(TAG, "Internet status: $hasInternet")

            suspend fun smartTranslate(text: String): String {
                if (text.isBlank()) return ""

                // Якщо текст занадто довгий для хмари або немає інтернету -> ML Kit
                if (text.length > 450 || !hasInternet) {
                    Log.d(TAG, "Текст > 450 симв. або немає мережі -> ML Kit")
                    return offlineTranslator.translate(text).getOrNull() ?: text
                }

                // Якщо текст короткий і є інтернет -> Хмара
                val cloudResult = cloudTranslator.translate(text)
                if (cloudResult.isSuccess) {
                    val resultText = cloudResult.getOrNull() ?: text
                    // Захист від того, що хмара поверне помилку текстом
                    if (!resultText.contains("LIMIT EXCEEDED")) {
                        return resultText
                    }
                }

                // Якщо хмара впала (наприклад, ліміт на день) -> ML Kit
                Log.e(TAG, "Cloud failed -> ML Kit fallback")
                return offlineTranslator.translate(text).getOrNull() ?: text
            }

            // 4. ПЕРЕКЛАД
            Log.d(TAG, "Починаємо переклад...")
            val translatedTitle = smartTranslate(titleToTranslate)
            Log.d(TAG, "Translated Title: $translatedTitle")

            // MyMemory має ліміт 500 слів за запит, тому перекладаємо абзацами!
            val paragraphs = textToTranslate.split("\n\n")
            val translatedParagraphs = mutableListOf<String>()

            for ((index, paragraph) in paragraphs.withIndex()) {
                Log.d(TAG, "Переклад абзацу ${index + 1}/${paragraphs.size}")
                translatedParagraphs.add(smartTranslate(paragraph))
            }

            val finalTranslatedText = translatedParagraphs.joinToString("\n\n")

            // 6. ЗБЕРЕЖЕННЯ
            Log.d(TAG, "Зберігаємо переклад в БД для URL: $url")
            repository.updateArticleTranslation(url, finalTranslatedText, translatedTitle)
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Fatal Translation Error", e)
            Result.failure(e)
        }
    }
}