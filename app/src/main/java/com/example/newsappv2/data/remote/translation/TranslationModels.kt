package com.example.newsappv2.data.remote.translation

import com.google.gson.annotations.SerializedName

data class MyMemoryResponse(
    @SerializedName("responseData")
    val responseData: ResponseData?
)

data class ResponseData(
    @SerializedName("translatedText")
    val translatedText: String?
)