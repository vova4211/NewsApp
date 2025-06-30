package com.example.newsappv2.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.newsappv2.data.local.db.NewsDatabase
import com.example.newsappv2.data.local.db.entities.ArticleEntity
import com.example.newsappv2.data.model.NewsResponse
import com.example.newsappv2.data.remote.NewsApiService
import com.example.newsappv2.data.remote.paging.NewsRemoteMediator
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class NetworkNewsRepository(
    private val newsApiService: NewsApiService,
    private val apiKey : String,
    private val database: NewsDatabase
) : NewsRepository {


    override fun getHomeNewsFromDb(): PagingSource<Int, ArticleEntity> =
        database.articleDao().getHomeNews()

    override fun getCategoryNewsFromDb(category: String): PagingSource<Int, ArticleEntity> =
        database.articleDao().getCategoryNews(category)

    override suspend fun clearHomeNews() = database.articleDao().clearHomeNews()
    override suspend fun clearCategoryNews(category: String) =
        database.articleDao().clearCategoryNews(category)

    override suspend fun insertArticles(articles: List<ArticleEntity>) =
        database.articleDao().insertArticles(articles)


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
    override fun getSearchNewsPager(query: String) : Flow<PagingData<ArticleEntity>> {
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
                category = null
            ),
            pagingSourceFactory = {
                database.articleDao().getHomeNews()
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
                category = category
            ),
            pagingSourceFactory = {
                database.articleDao().getCategoryNews(category)
            }
        ).flow
    }


}