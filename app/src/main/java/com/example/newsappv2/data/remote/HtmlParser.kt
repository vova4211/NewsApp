package com.example.newsappv2.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HtmlParser @Inject constructor() {

    suspend fun extractArticleText(url: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(url)
                    .timeout(10000)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get()

                val paragraphs = document.select("p")

                val articleText = StringBuilder()
                for (p in paragraphs) {
                    val text = p.text()
                    if (text.length > 30) {
                        articleText.append(text).append("\n\n")
                    }
                }

                val finalResult = articleText.toString().trim()

                if (finalResult.isNotEmpty()) {
                    Result.success(finalResult)
                } else {
                    Result.failure(Exception("Не вдалося знайти текст статті на сторінці"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}