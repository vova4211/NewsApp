package com.example.newsappv2.data.remote.translation

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface CloudTranslationApi {
    @POST("language/translate/v2")
    suspend fun translate(
        @Query("q") text: String,
        @Query("target") targetLanguage: String,
        @Query("key") apiKey: String,
        @Query("format") format: String = "text"
    ): Response<GoogleTranslationResponse>
}