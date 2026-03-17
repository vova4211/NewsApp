package com.example.newsappv2.data.remote.translation

import com.google.gson.annotations.SerializedName

data class GoogleTranslationResponse(
    @SerializedName("data")
    val data: GoogleTranslationData?
)

data class GoogleTranslationData(
    @SerializedName("translations")
    val translations: List<GoogleTranslation>?
)

data class GoogleTranslation(
    @SerializedName("translatedText")
    val translatedText: String?
)