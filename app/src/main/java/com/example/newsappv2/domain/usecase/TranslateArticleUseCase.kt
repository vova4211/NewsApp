package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.mlkit.OfflineTranslator
import com.example.newsappv2.data.repository.NewsRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class TranslateArticleUseCase @Inject constructor(
    private val translator: OfflineTranslator,
    private val repository: NewsRepository
) {
    suspend operator fun invoke(url: String): Result<Unit> {
        return try {
            val articleEntity = repository.getArticleByUrl(url).firstOrNull()
            val textToTranslate = articleEntity?.fullText
            val titleToTranslate = articleEntity?.title

            if (textToTranslate.isNullOrBlank()) {
                return Result.failure(Exception("Немає тексту для перекладу."))
            }

            val modelResult = translator.downloadModelIfNeeded()
            if (modelResult.isFailure) {
                return Result.failure(modelResult.exceptionOrNull() ?: Exception("Помилка моделі"))
            }

            val translatedTitle = if (!titleToTranslate.isNullOrBlank()) {
                translator.translate(titleToTranslate).getOrNull() ?: titleToTranslate
            } else {
                "Без заголовка"
            }

            val paragraphs = textToTranslate.split("\n\n")
            val translatedParagraphs = paragraphs.map { paragraph ->
                if (paragraph.isNotBlank()) {
                    translator.translate(paragraph).getOrNull() ?: paragraph
                } else {
                    ""
                }
            }

            val finalTranslatedText = translatedParagraphs.joinToString("\n\n")

            repository.updateArticleTranslation(url, finalTranslatedText, translatedTitle)
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}