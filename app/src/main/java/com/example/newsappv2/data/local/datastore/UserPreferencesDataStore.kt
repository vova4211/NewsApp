package com.example.newsappv2.data.local.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.newsappv2.util.Category
import com.example.newsappv2.util.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore

    val uiLanguage: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[UI_LANGUAGE] ?: "uk"
        }

    suspend fun saveUiLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[UI_LANGUAGE] = languageCode
        }
    }

    val searchQuery: Flow<String> = dataStore.data
        .catch {
            if ( it is IOException) {
                Log.e(TAG,"Error reading preferences.", it )
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[SEARCH_QUERY] ?: ""
        }

    suspend fun saveLastQuery(query: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_QUERY] = query
        }
    }

    val selectCategory: Flow<Category> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val categoryName = preferences[SELECTED_CATEGORY]
            Category.fromString(categoryName ?: "") ?: Category.BUSINESS
        }

    suspend fun saveSelectedCategory(category: Category) {
        dataStore.edit { preferences ->
            preferences[SELECTED_CATEGORY] = category.apiValue
        }
    }

    val targetLanguage: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[TARGET_LANGUAGE] ?: "en"
        }

    val themeMode: Flow<ThemeMode> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val themeName = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            ThemeMode.valueOf(themeName)
        }

    suspend fun saveTargetLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[TARGET_LANGUAGE] = languageCode
        }
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) emit(emptyPreferences()) else throw it
        }
        .map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true // За замовчуванням true (перший запуск)
        }

    val userName: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) emit(emptyPreferences()) else throw it
        }
        .map { preferences ->
            preferences[USER_NAME] ?: ""
        }

    val userAvatarUri: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) emit(emptyPreferences()) else throw it
        }
        .map { preferences ->
            preferences[USER_AVATAR_URI] ?: ""
        }

    suspend fun completeOnboarding(name: String, avatarUri: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_AVATAR_URI] = avatarUri
            preferences[IS_FIRST_LAUNCH] = false
        }
    }

    suspend fun saveThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    suspend fun updateProfile(name: String, avatarUri: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_AVATAR_URI] = avatarUri
        }
    }

    companion object {
        private  val SEARCH_QUERY = stringPreferencesKey("search_query")
        private val SELECTED_CATEGORY = stringPreferencesKey("selected_category")
        private val TARGET_LANGUAGE = stringPreferencesKey("target_language")
        private val UI_LANGUAGE = stringPreferencesKey("ui_language")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_AVATAR_URI = stringPreferencesKey("user_avatar_uri")
        private const val TAG = "UserPreferencesRepo"
    }
}