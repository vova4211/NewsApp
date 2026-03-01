package com.example.newsappv2.data.mlkit

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineTranslator @Inject constructor() {
    private val option = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.UKRAINIAN)
        .build()

    private val englishUkrainianTranslator = Translation.getClient(option)

    suspend fun downloadModelIfNeeded(): Result<Unit> {
        val conditions = DownloadConditions.Builder()
            .build()

        return try {
            englishUkrainianTranslator.downloadModelIfNeeded(conditions).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun translate(text: String): Result<String> {
        return  try {
            val translatedText = englishUkrainianTranslator.translate(text).await()
            Result.success(translatedText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}