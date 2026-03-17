package com.example.newsappv2.data.mlkit

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineTranslator @Inject constructor() {

    private fun getTranslator(targetLanguage: String): Translator {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLanguage)
            .build()
        return Translation.getClient(options)
    }

    suspend fun downloadModelIfNeeded(targetLanguage: String = TranslateLanguage.UKRAINIAN): Result<Unit> {
        return try {
            val translator = getTranslator(targetLanguage)
            translator.downloadModelIfNeeded().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun translate(text: String, targetLanguage: String = TranslateLanguage.UKRAINIAN): Result<String> {
        return try {
            val translator = getTranslator(targetLanguage)
            val translatedText = translator.translate(text).await()
            Result.success(translatedText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}