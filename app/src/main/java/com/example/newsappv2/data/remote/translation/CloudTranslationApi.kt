package com.example.newsappv2.data.remote.translation


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CloudTranslationApi {
    @GET("get")
    suspend fun translate(
        @Query("q") text: String,
        @Query("langpair") langPair: String = "en|uk"
    ): Response<MyMemoryResponse>
}