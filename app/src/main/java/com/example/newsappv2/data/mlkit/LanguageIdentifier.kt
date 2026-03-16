package com.example.newsappv2.data.mlkit

import com.google.mlkit.nl.languageid.LanguageIdentification
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageIdentifier @Inject constructor() {

    private val languageIdClient = LanguageIdentification.getClient()

    suspend fun identify(text: String): String {
        return try {
            languageIdClient.identifyLanguage(text).await()
        } catch (e: Exception) {
            "und"
        }
    }
}