package com.example.newsappv2.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsappv2.data.local.db.entities.RemoteKeys

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(remoteKey: RemoteKeys)

    @Query("SELECT * FROM remote_keys WHERE queryOrCategory = :queryOrCategory")
    suspend fun remoteKeysId(queryOrCategory: String): RemoteKeys?

    @Query("DELETE FROM remote_keys WHERE queryOrCategory = :queryOrCategory")
    suspend fun clearRemoteKeys(queryOrCategory: String)
}