package com.example.newsappv2.data.model


data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)