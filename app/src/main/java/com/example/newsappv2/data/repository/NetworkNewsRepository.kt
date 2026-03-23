package com.example.newsappv2.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.data.local.db.NewsDatabase
import com.example.newsappv2.data.local.db.entities.ArticleEntity
import com.example.newsappv2.data.mlkit.OfflineTranslator
import com.example.newsappv2.data.model.NewsResponse
import com.example.newsappv2.data.remote.NewsApiService
import com.example.newsappv2.data.remote.paging.NewsRemoteMediator
import com.example.newsappv2.data.remote.translation.CloudTranslator
import com.example.newsappv2.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class NetworkNewsRepository(
    private val newsApiService: NewsApiService,
    private val apiKey: String,
    private val database: NewsDatabase,
    private val cloudTranslator: CloudTranslator,
    private val offlineTranslator: OfflineTranslator,
    private val networkMonitor: NetworkMonitor,
    private val userPreferences: UserPreferencesDataStore
) : NewsRepository {


    override fun getHomeNewsFromDb(query: String): PagingSource<Int, ArticleEntity> =
        database.articleDao().getHomeNews(query)

    override fun getCategoryNewsFromDb(category: String): PagingSource<Int, ArticleEntity> =
        database.articleDao().getCategoryNews(category)

    override fun getArticleByUrl(url: String) = database.articleDao().getArticleByUrl(url)

    override suspend fun clearHomeNews(query: String) = database.articleDao().clearHomeNews(query)

    override suspend fun clearCategoryNews(category: String) =
        database.articleDao().clearCategoryNews(category)

    override fun getSavedArticles(): Flow<List<ArticleEntity>> {
        return database.articleDao().getSavedArticles()
    }

    override suspend fun updateSavedStatus(url: String, isSaved: Boolean) {
        database.articleDao().updateSavedStatus(url, isSaved)
    }

    override suspend fun insertArticles(articles: List<ArticleEntity>) =
        database.articleDao().insertArticles(articles)

    override suspend fun updateArticleFullText(url: String, fullText: String) {
        database.articleDao().updateArticleFullText(url, fullText)
    }

    override suspend fun getArticlesForSmartSync(query: String): List<ArticleEntity> {
        return database.articleDao().getArticlesForSmartSync(query)
    }

    override suspend fun clearUnsavedArticles() = database.articleDao().clearUnsavedArticles()

    override suspend fun updateArticleTranslation(url: String, translatedText: String, translatedTitle: String) {
        database.articleDao().updateArticleTranslation(url, translatedText, translatedTitle)
    }

    override suspend fun searchNews(
        query: String?,
        language: String,
        pageSize: Int,
        page: Int
    ): Response<NewsResponse> {
        return newsApiService.searchNews(
            apiKey = apiKey,
            query = query,
            language = language,
            pageSize = pageSize,
            page = page
        )
    }

    override suspend fun getTopHeadlines(
        category: String?,
        country: String,
        pageSize: Int,
        page: Int
    ): Response<NewsResponse> {
        return newsApiService.getTopHeadlines(
            apiKey = apiKey,
            category = category,
            country = country,
            pageSize = pageSize,
            page = page
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getSearchNewsPager(query: String): Flow<PagingData<ArticleEntity>> {
        Log.d("Repository", "Start pager with query: $query")
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = NewsRemoteMediator(
                query = query,
                repository = this,
                database = database,
                category = null,
                cloudTranslator = cloudTranslator,
                offlineTranslator = offlineTranslator,
                networkMonitor = networkMonitor,
                userPreferences = userPreferences
            ),
            pagingSourceFactory = {
                database.articleDao().getHomeNews(query)
            }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getCategoryNewsPager(category: String): Flow<PagingData<ArticleEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = NewsRemoteMediator(
                query = null,
                repository = this,
                database = database,
                category = category,
                cloudTranslator = cloudTranslator,
                offlineTranslator = offlineTranslator,
                networkMonitor = networkMonitor,
                userPreferences = userPreferences
            ),
            pagingSourceFactory = {
                database.articleDao().getCategoryNews(category)
            }
        ).flow
    }
}