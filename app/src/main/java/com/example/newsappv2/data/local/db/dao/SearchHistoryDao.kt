package com.example.newsappv2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.newsappv2.data.local.db.entities.SearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(searchItem: SearchQueryEntity) // Змінили ім'я параметра

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 5")
    fun getRecentQueries(): Flow<List<SearchQueryEntity>>

    @Query("DELETE FROM search_history WHERE searchQuery NOT IN (SELECT searchQuery FROM search_history ORDER BY timestamp DESC LIMIT 5)")
    suspend fun deleteOldQueries()

    @Transaction
    suspend fun insertAndCleanup(searchItem: SearchQueryEntity) {
        insertQuery(searchItem)
        deleteOldQueries()
    }
}