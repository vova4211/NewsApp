package com.example.newsappv2.di

import android.content.Context
import com.example.newsappv2.data.local.dao.SearchHistoryDao
import com.example.newsappv2.data.local.db.NewsDatabase
import com.example.newsappv2.data.local.db.dao.ArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDatabase {
        return NewsDatabase.getDatabase(context)
    }

    @Provides
    fun provideArticleDao(database: NewsDatabase): ArticleDao {
        return  database.articleDao()
    }

    @Provides
    fun provideSearchHistoryDao(database: NewsDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }
}