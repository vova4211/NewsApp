package com.example.newsappv2.domain.model

data class Article(
    val title: String,
    val description: String,
    val author: String,
    val sourceName: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val fullText: String? = null,
    val translatedText: String? = null,
    val translatedTitle: String? = null,
    val isSaved: Boolean = false
)