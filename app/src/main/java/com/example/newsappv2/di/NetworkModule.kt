package com.example.newsappv2.di

import com.example.newsappv2.data.remote.NewsApiService
import com.example.newsappv2.util.Constants.BASE_URL
import com.example.newsappv2.util.Constants.TRANSLATE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService {
        return  retrofit.create(NewsApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideCloudTranslationApi(): com.example.newsappv2.data.remote.translation.CloudTranslationApi {
        return Retrofit.Builder()
            .baseUrl(TRANSLATE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(com.example.newsappv2.data.remote.translation.CloudTranslationApi::class.java)
    }
}