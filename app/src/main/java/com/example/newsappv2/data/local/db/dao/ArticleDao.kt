package com.example.newsappv2.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsappv2.data.local.db.entities.ArticleEntity

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles:(List<ArticleEntity>))
//    @Query("SELECT * FROM articles WHERE category IS NULL")
//    fun getHomeNews(): PagingSource<Int, ArticleEntity>
//    @Query("SELECT * FROM articles WHERE category = :category")
//    fun getCategoryNews(category: String): PagingSource<Int, ArticleEntity>
//    @Query("DELETE FROM articles WHERE category IS NULL")
//    suspend fun clearHomeNews()
//    @Query("DELETE FROM articles WHERE category = :category")
//    suspend fun clearCategoryNews(category: String)

    @Query("SELECT * FROM articles WHERE type = 'search' AND `query` = :query")
    fun getHomeNews(query: String): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE type = 'category' AND category = :category")
    fun getCategoryNews(category: String): PagingSource<Int, ArticleEntity>

    @Query("DELETE FROM articles WHERE type = 'search' AND `query` = :query")
    suspend fun clearHomeNews(query: String)

    @Query("DELETE FROM articles WHERE type = 'category' AND category = :category")
    suspend fun clearCategoryNews(category: String)


}