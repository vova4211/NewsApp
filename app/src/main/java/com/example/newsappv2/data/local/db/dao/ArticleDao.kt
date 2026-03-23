package com.example.newsappv2.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsappv2.data.local.db.entities.ArticleEntity

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles WHERE type = 'search' AND `query` = :query")
    fun getHomeNews(query: String): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE type = 'category' AND category = :category")
    fun getCategoryNews(category: String): PagingSource<Int, ArticleEntity>

    @Query("DELETE FROM articles WHERE type = 'search' AND `query` = :query")
    suspend fun clearHomeNews(query: String)

    @Query("DELETE FROM articles WHERE type = 'category' AND category = :category")
    suspend fun clearCategoryNews(category: String)

    @Query("UPDATE articles SET full_text = :fullText WHERE url = :url")
    suspend fun updateArticleFullText(url: String, fullText: String)

    @Query("UPDATE articles SET translated_text = :translatedText, translated_title = :translatedTitle WHERE url = :url")
    suspend fun updateArticleTranslation(url: String, translatedText: String, translatedTitle: String)

    @Query("SELECT * FROM articles WHERE url = :url LIMIT 1")
    fun getArticleByUrl(url: String): kotlinx.coroutines.flow.Flow<ArticleEntity>

    @Query("DELETE FROM articles WHERE is_saved = 0 AND full_text IS NULL AND translated_text IS NULL")
    suspend fun clearUnsavedArticles()

    @Query("SELECT * FROM articles WHERE is_saved = 1 ORDER BY published_at DESC")
    fun getSavedArticles(): kotlinx.coroutines.flow.Flow<List<ArticleEntity>>

    @Query("UPDATE articles SET is_saved = :isSaved WHERE url = :url")
    suspend fun updateSavedStatus(url: String, isSaved: Boolean)

    @Query("SELECT * FROM articles WHERE type = 'search' AND `query` = :query AND full_text IS NULL ORDER BY published_at DESC LIMIT 5")
    suspend fun getArticlesForSmartSync(query: String): List<ArticleEntity>

    @Query("""
        SELECT * FROM articles 
        WHERE is_saved = 1 
        AND (title LIKE '%' || :searchQuery || '%' 
             OR description LIKE '%' || :searchQuery || '%' 
             OR full_text LIKE '%' || :searchQuery || '%') 
        ORDER BY published_at DESC
    """)
    fun searchSavedArticles(searchQuery: String): kotlinx.coroutines.flow.Flow<List<ArticleEntity>>
}