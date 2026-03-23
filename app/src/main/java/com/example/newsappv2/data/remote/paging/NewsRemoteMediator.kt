package com.example.newsappv2.data.remote.paging

import android.util.Log
import androidx.datastore.core.IOException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.data.local.db.NewsDatabase
import com.example.newsappv2.data.local.db.entities.ArticleEntity
import com.example.newsappv2.data.mlkit.OfflineTranslator
import com.example.newsappv2.data.remote.translation.CloudTranslator
import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.util.NetworkMonitor
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val query: String?,
    private val database: NewsDatabase,
    private val repository: NewsRepository,
    private val category: String?,
    private val cloudTranslator: CloudTranslator,
    private val offlineTranslator: OfflineTranslator,
    private val networkMonitor: NetworkMonitor,
    private val userPreferences: UserPreferencesDataStore
) : RemoteMediator<Int, ArticleEntity>() {

    private val TAG = "NewsRemoteMediator"

    @ExperimentalPagingApi
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    if (state.pages.isEmpty()) {
                        return MediatorResult.Success(endOfPaginationReached = false)
                    }
                    state.pages.size + 1
                }
            }

            val response = if (category != null)
                repository.getTopHeadlines(category = category, page = page, pageSize = state.config.pageSize)
            else
                repository.searchNews(query = query, page = page, pageSize = state.config.pageSize)

            if (!response.isSuccessful || response.body() == null) {
                return MediatorResult.Error(HttpException(response))
            }

            val articles = response.body()?.articles.orEmpty()
            val targetLangCode = userPreferences.targetLanguage.first()
            val hasInternet = networkMonitor.isOnline()

            offlineTranslator.downloadModelIfNeeded(targetLangCode)

            suspend fun smartTranslate(text: String?, type: String): String? {
                if (text.isNullOrBlank()) return text
                return try {
                    if (!hasInternet) {
                        offlineTranslator.translate(text, targetLangCode).getOrNull() ?: text
                    } else {
                        val cloudResult = cloudTranslator.translate(text, targetLangCode)
                        if (cloudResult.isSuccess) {
                            cloudResult.getOrNull() ?: text
                        } else {
                            Log.e(TAG, "Cloud API ПОМИЛКА для $type: ${cloudResult.exceptionOrNull()?.message}")
                            offlineTranslator.translate(text, targetLangCode).getOrNull() ?: text
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Критична помилка перекладу $type", e)
                    text
                }
            }

            val entities = kotlinx.coroutines.coroutineScope {
                articles.map { article ->
                    async {
                        val validUrl = article.url ?: return@async null

                        val translatedTitle = smartTranslate(article.title, "Title") ?: article.title ?: "Без заголовка"
                        val translatedDesc = smartTranslate(article.description, "Description") ?: article.description

                        ArticleEntity(
                            sourceName = article.source.name,
                            author = article.author,
                            title = translatedTitle,
                            description = translatedDesc,
                            url = validUrl,
                            urlToImage = article.urlToImage,
                            publishedAt = article.publishedAt,
                            category = category,
                            query = query,
                            type = if (category != null) "category" else "search"
                        )
                    }
                }.awaitAll().filterNotNull()
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    repository.clearUnsavedArticles()
                }
                repository.insertArticles(entities)
            }

            MediatorResult.Success(endOfPaginationReached = articles.isEmpty())

        } catch (e: IOException) {
            Log.e(TAG, "Помилка мережі", e)
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e(TAG, "Помилка API", e)
            MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e(TAG, "Невідома помилка", e)
            MediatorResult.Error(e)
        }
    }
}