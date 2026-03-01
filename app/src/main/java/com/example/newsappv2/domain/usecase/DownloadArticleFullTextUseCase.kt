package com.example.newsappv2.domain.usecase

import com.example.newsappv2.data.remote.HtmlParser
import com.example.newsappv2.data.repository.NewsRepository
import javax.inject.Inject

class DownloadArticleFullTextUseCase @Inject constructor(
    private val htmlParser: HtmlParser,
    private val repository: NewsRepository
) {
    suspend operator fun invoke(url: String): Result<Unit> {
        val result = htmlParser.extractArticleText(url)

        return if (result.isSuccess){
            val text = result.getOrNull()
            if (!text.isNullOrBlank()){
                repository.updateArticleFullText(url, text)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Текст статті порожній"))
            }
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Невідома помилка парсингу"))
        }
    }
}