package com.example.newsappv2.di

import com.example.newsappv2.BuildConfig
import com.example.newsappv2.data.local.db.NewsDatabase
import com.example.newsappv2.data.remote.NewsApiService
import com.example.newsappv2.data.repository.NetworkNewsRepository
import com.example.newsappv2.data.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule  {

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsApiService: NewsApiService,
        database: NewsDatabase
    ) : NewsRepository {
        return NetworkNewsRepository(
            newsApiService = newsApiService,
            apiKey = BuildConfig.API_KEY,
            database = database
        )
    }

}