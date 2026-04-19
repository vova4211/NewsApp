package com.example.newsappv2.data.remote.translation

import android.text.Html
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudTranslator @Inject constructor(
    private val api: CloudTranslationApi
) {
    suspend fun translate(text: String, targetLanguage: String = "uk"): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.translate(
                    text = text,
                    targetLanguage = targetLanguage,
                    apiKey = com.example.newsappv2.BuildConfig.TRANSLATE
                )

                if (response.isSuccessful) {
                    val rawTranslatedText = response.body()?.data?.translations?.firstOrNull()?.translatedText

                    if (!rawTranslatedText.isNullOrBlank()) {
                        val cleanText = Html.fromHtml(rawTranslatedText, Html.FROM_HTML_MODE_LEGACY).toString()
                        Result.success(cleanText)
                    } else {
                        Result.failure(Exception("Порожня відповідь від Google API"))
                    }
                } else {
                    Result.failure(Exception("Помилка Google Cloud API: ${response.code()} - ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}