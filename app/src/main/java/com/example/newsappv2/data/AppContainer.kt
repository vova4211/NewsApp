package com.example.newsappv2.data

import com.example.newsappv2.data.repository.NewsRepository

interface AppContainer {
    val newsRepository : NewsRepository
}