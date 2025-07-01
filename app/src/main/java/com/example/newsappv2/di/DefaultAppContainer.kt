package com.example.newsappv2.di

import android.content.Context
import com.example.newsappv2.BuildConfig
import com.example.newsappv2.data.AppContainer
import com.example.newsappv2.data.local.db.NewsDatabase
import com.example.newsappv2.data.remote.NewsApiService
import com.example.newsappv2.data.repository.NetworkNewsRepository
import com.example.newsappv2.data.repository.NewsRepository
import com.example.newsappv2.util.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val baseUrl = BASE_URL

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }

    override val newsRepository: NewsRepository by lazy {
        val database = NewsDatabase.getDatabase(context)
        NetworkNewsRepository(retrofitService, BuildConfig.API_KEY, database)
    }
}