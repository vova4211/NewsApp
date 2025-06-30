package com.example.newsappv2.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsappv2.data.model.Article
import com.example.newsappv2.data.repository.NewsRepository

class NewsPagingSource(
    private val repository: NewsRepository,
    private val query: String
) : PagingSource<Int, Article>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {

        val page = params.key ?: 1
        return try {
            val response = repository.searchNews(query, page = page, pageSize = params.loadSize)
            if (response.isSuccessful) {
                val articles = response.body()?.articles?.distinctBy { it.url } ?: emptyList()

                LoadResult.Page(
                    data = articles,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (articles.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { position ->
            val page = state.closestPageToPosition(position)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }
}
