package com.example.newsappv2

import android.app.Application
import com.example.newsappv2.data.AppContainer
import com.example.newsappv2.data.local.datastore.UserPreferencesDataStore
import com.example.newsappv2.data.local.datastore.dataStore
import com.example.newsappv2.di.DefaultAppContainer

class NewsApplication : Application() {
    lateinit var container: AppContainer
    lateinit var userPreferencesDataStore: UserPreferencesDataStore

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        userPreferencesDataStore = UserPreferencesDataStore(dataStore)
    }
}