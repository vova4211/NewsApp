package com.example.newsappv2.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newsappv2.NewsApplication
import com.example.newsappv2.util.Constants.DEFAULT_QUERY
import com.example.newsappv2.util.notifications.NotificationHelper
import retrofit2.HttpException

class NewsCheckWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val appContainer = (context.applicationContext as NewsApplication).container

        val prefs = context.getSharedPreferences("news_prefs", Context.MODE_PRIVATE)
        val lastSeenArticleUrl = prefs.getString("last_article_url", null)
        val lastTotalResults = prefs.getInt("last_total_results", -1)

        return try {
            val response = appContainer.newsRepository.searchNews(query = DEFAULT_QUERY)

            if (response.isSuccessful && !response.body()?.articles.isNullOrEmpty()) {
                val body = response.body()!!
                val latestArticle = body.articles.firstOrNull()
                val latestArticleUrl = latestArticle?.url
                val totalResults = body.totalResults

                val isNewTotalResults = totalResults > lastTotalResults
                val isNewArticle = latestArticleUrl != null && latestArticleUrl != lastSeenArticleUrl

                if (isNewTotalResults || isNewArticle) {
                    prefs.edit()
                        .putString("last_article_url", latestArticleUrl)
                        .putInt("last_total_results", totalResults)
                        .apply()


                    NotificationHelper.showNotification(
                        context,
                        title = "New news!",
                        message = "Check the latest news now!"
                    )
                }
            }

            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
