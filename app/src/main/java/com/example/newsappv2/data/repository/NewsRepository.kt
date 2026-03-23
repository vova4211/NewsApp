package com.example.newsappv2.data.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.newsappv2.data.local.db.entities.ArticleEntity
import com.example.newsappv2.data.model.NewsResponse
import com.example.newsappv2.util.Constants.DEFAULT_COUNTRY
import com.example.newsappv2.util.Constants.DEFAULT_LANGUAGE
import com.example.newsappv2.util.Constants.DEFAULT_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface NewsRepository {
    suspend fun getTopHeadlines(
        category: String? = null,
        country: String = DEFAULT_COUNTRY,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        page: Int = 1
    ) : Response<NewsResponse>

    suspend fun searchNews(
        query: String? = null,
        language : String = DEFAULT_LANGUAGE,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        page: Int = 1
    ) : Response<NewsResponse>

    fun getArticleByUrl(url: String): kotlinx.coroutines.flow.Flow<ArticleEntity>
    fun getSearchNewsPager(query: String) : Flow<PagingData<ArticleEntity>>
    fun getCategoryNewsPager(category: String): Flow<PagingData<ArticleEntity>>
    fun getHomeNewsFromDb(query: String) : PagingSource<Int, ArticleEntity>
    fun getCategoryNewsFromDb(category: String) : PagingSource<Int, ArticleEntity>
    suspend fun clearHomeNews(query: String)
    suspend fun clearCategoryNews(category: String)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    suspend fun updateArticleFullText(url: String, fullText: String)

    suspend fun updateArticleTranslation(url: String, translatedText: String, translatedTitle: String)

    suspend fun clearUnsavedArticles()
    fun getSavedArticles(): Flow<List<ArticleEntity>>

    fun searchSavedArticles(searchQuery: String): Flow<List<ArticleEntity>>
    suspend fun updateSavedStatus(url: String, isSaved: Boolean)

    suspend fun getArticlesForSmartSync(query: String): List<ArticleEntity>


}