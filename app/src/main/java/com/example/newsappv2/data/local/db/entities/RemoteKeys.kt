package com.example.newsappv2.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val queryOrCategory: String,
    val nextKey: Int?
)