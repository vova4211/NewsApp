package com.example.newsappv2.data.remote.paging

import androidx.datastore.core.IOException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsappv2.data.local.db.NewsDatabase
import com.example.newsappv2.data.local.db.entities.ArticleEntity
import com.example.newsappv2.data.repository.NewsRepository
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val query: String?,
    private val  database: NewsDatabase,
    private val repository: NewsRepository,
    private val  category: String?

) : RemoteMediator<Int, ArticleEntity>() {

    @ExperimentalPagingApi
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.APPEND -> {
                    val itemCount = state.pages.sumOf { it.data.size }
                    (itemCount / state.config.pageSize) + 1
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            }


            val response = if (category != null)
                repository.getTopHeadlines(
                    category = category,
                    page = page,
                    pageSize = state.config.pageSize
                )
            else
                repository.searchNews(
                    query = query,
                    page = page,
                    pageSize = state.config.pageSize
                )

            if (!response.isSuccessful || response.body() == null) {
                return  MediatorResult.Error(HttpException(response))
            }

            val articles = response.body()?.articles.orEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    if (category == null) {
                        repository.clearHomeNews()
                    } else {
                        repository.clearCategoryNews(category)
                    }
                }

                val entities = articles.map {
                    com.example.newsappv2.data.local.db.entities.ArticleEntity(
                        sourceName = it.source.name,
                        author = it.author,
                        title = it.title,
                        description = it.description,
                        url = it.url,
                        urlToImage = it.urlToImage,
                        publishedAt = it.publishedAt,
                        category = category
                    )
                }
                repository.insertArticles(entities)
            }

            return  MediatorResult.Success(endOfPaginationReached = articles.isEmpty())

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}