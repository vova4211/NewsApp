package com.example.newsappv2.data.worker

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newsappv2.NewsApplication
import com.example.newsappv2.data.local.datastore.dataStore
import com.example.newsappv2.util.Constants.DEFAULT_QUERY
import com.example.newsappv2.util.notifications.NotificationHelper
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class NewsCheckWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val TAG = "NewsCheckWorker"
    private val LAST_ARTICLE_URL_KEY = stringPreferencesKey("last_article_url")
    private val LAST_ARTICLE_DATE_KEY = stringPreferencesKey("last_article_date")

    override suspend fun doWork(): Result {
        val appContainer = ( context.applicationContext as NewsApplication).container
        val dataStore = context.dataStore

        try {
            val response = appContainer.newsRepository.searchNews(query = DEFAULT_QUERY)

            if (response.isSuccessful) {
                val body = response.body()
                val latestArticle = body?.articles?.firstOrNull()

                if (latestArticle == null) {
                    Log.d(TAG, "No articles found")
                    return Result.success()
                }

                val latestUrl = latestArticle.url ?: ""
                val latestDate = latestArticle.publishedAt ?: ""

                val prefs = dataStore.data.first()
                val savedUrl = prefs[LAST_ARTICLE_URL_KEY]
                val savedDate = prefs[LAST_ARTICLE_DATE_KEY]

                val isNewUrl = latestUrl.isNotEmpty() && latestUrl != savedUrl
                val isNewDate = latestDate.isNotEmpty() && latestDate != savedDate

                if (isNewUrl || isNewDate) {
                    dataStore.edit { settings ->
                        settings[LAST_ARTICLE_URL_KEY] = latestUrl
                        settings[LAST_ARTICLE_DATE_KEY] = latestDate
                    }

                    NotificationHelper.showNotification(
                        context = context,
                        title = "📰 New Article Available!",
                        message = latestArticle.title ?: "Check the latest news now!"
                    )

                    Log.d(TAG, "New article detected. Notification sent.")
                } else {
                    Log.d(TAG, "No new article. Skipping notification.")
                }
                return Result.success()
            } else {
                Log.e(TAG, "Response not successful: ${response.code()}")
                return Result.retry()
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error: ${e.message}", e)
            return Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}", e)
            return Result.failure()
        }
    }

}
