package com.example.newsappv2.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchQueryEntity(
    @PrimaryKey
    val searchQuery: String,
    val timestamp: Long
)