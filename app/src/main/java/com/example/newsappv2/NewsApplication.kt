package com.example.newsappv2

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.newsappv2.data.AppContainer
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.data.local.datastore.dataStore
import com.example.newsappv2.data.worker.NewsCheckWorker
import com.example.newsappv2.di.DefaultAppContainer
import java.util.concurrent.TimeUnit

class NewsApplication : Application() {
    lateinit var container: AppContainer
    lateinit var userPreferencesDataStore: UserPreferencesDataStore

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        userPreferencesDataStore = UserPreferencesDataStore(dataStore)


        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val periodicWorkRequest = PeriodicWorkRequestBuilder<NewsCheckWorker>(
            6, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "NewsCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}