package com.example.newsappv2.data.remote.translation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudTranslator @Inject constructor(
    private val api: CloudTranslationApi
) {
    suspend fun translate(text: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.translate(text)

                if (response.isSuccessful) {
                    val translatedText = response.body()?.responseData?.translatedText

                    if (!translatedText.isNullOrBlank() &&
                        !translatedText.contains("MYMEMORY WARNING") &&
                        !translatedText.contains("LIMIT EXCEEDED")
                    ) {
                        Result.success(translatedText)
                    } else {
                        Result.failure(Exception("Перевищено ліміт хмари або порожня відповідь"))
                    }
                } else {
                    Result.failure(Exception("Помилка хмарного API: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}