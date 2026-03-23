package com.example.newsappv2.util

import com.example.newsappv2.data.local.db.entities.ArticleEntity
import com.example.newsappv2.domain.model.Article

fun ArticleEntity.toDomainArticle(): Article {
    return Article(
        title = this.title ?: "Unknown Title",
        description = this.description ?: "",
        author = this.author ?: "Unknown Author",
        sourceName = this.sourceName ?: "Unknown Source",
        url = this.url ?: "",
        urlToImage = this.urlToImage ?: "",
        publishedAt = this.publishedAt ?: "",
        fullText = this.fullText,
        translatedText = this.translatedText,
        translatedTitle = this.translatedTitle,
        isSaved = this.isSaved
    )
}