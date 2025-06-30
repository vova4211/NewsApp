package com.example.newsappv2.data.remote

import com.example.newsappv2.data.model.NewsResponse
import com.example.newsappv2.util.Constants.DEFAULT_LANGUAGE
import com.example.newsappv2.util.Constants.DEFAULT_PAGE_SIZE
import com.example.newsappv2.util.Constants.DEFAULT_COUNTRY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("apiKey") apiKey: String,
        @Query("q") query: String? = null,
        @Query("sortBy") sortBy: String = "relevancy",
        @Query("language") language : String = DEFAULT_LANGUAGE,
        @Query("pageSize") pageSize: Int = DEFAULT_PAGE_SIZE,
        @Query("page") page: Int = 1
    ) : Response<NewsResponse>


    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("apiKey") apiKey: String,
        @Query("country") country: String = DEFAULT_COUNTRY,
        @Query("category") category: String? = null,
        @Query("pageSize") pageSize: Int = DEFAULT_PAGE_SIZE,
        @Query("page") page: Int = 1
    ) : Response<NewsResponse>


}