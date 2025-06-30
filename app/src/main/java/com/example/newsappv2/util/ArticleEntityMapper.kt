package com.example.newsappv2.util

import com.example.newsappv2.data.local.db.entities.ArticleEntity
import com.example.newsappv2.data.model.Article
import com.example.newsappv2.data.model.Source

fun ArticleEntity.toArticle(): Article {
    return Article(
        source = Source(id = null, name = this.sourceName),
        author = this.author,
        title = this.title,
        description = this.description,
        url = this.url,
        urlToImage = this.urlToImage,
        publishedAt = this.publishedAt
    )
}