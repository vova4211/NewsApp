package com.example.newsappv2.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsappv2.data.local.db.dao.ArticleDao
import com.example.newsappv2.data.local.db.entities.ArticleEntity
import com.example.newsappv2.data.local.db.entities.SearchQueryEntity
import com.example.newsappv2.data.local.dao.SearchHistoryDao
import com.example.newsappv2.data.local.db.entities.RemoteKeys

@Database(entities = [ArticleEntity::class, SearchQueryEntity::class, RemoteKeys::class], version = 9, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun remoteKeysDao(): RemoteKeysDao
    companion object {
        @Volatile
        private var instance: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, NewsDatabase::class.java, "news_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }

    }
}