package com.example.newsappv2.data.local.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.newsappv2.util.Category
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore
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

    val selectCategory : Flow<Category> = dataStore.data
        .catch {
            if ( it is IOException) {
                Log.e(TAG,"Error reading preferences.", it )
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
            preferences[SELECTED_CATEGORY] = category.categoryName
        }
    }

    companion object {
        private  val SEARCH_QUERY = stringPreferencesKey("search_query")
        private val SELECTED_CATEGORY = stringPreferencesKey("selected_category")
        private const val TAG = "UserPreferencesRepo"
    }
}