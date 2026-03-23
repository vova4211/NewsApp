package com.example.newsappv2.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.newsappv2.R
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.navigation.NavigationDestination
import com.example.newsappv2.ui.components.NewsCard
import com.example.newsappv2.ui.components.NewsCardSkeleton
import com.example.newsappv2.ui.components.OutlinedTextFieldHomeScreen
import com.example.newsappv2.util.PagingLoadStateHandler
import com.example.newsappv2.viewmodel.HomeViewModel

object HomeDestination: NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    onArticleClicked: (String) -> Unit,
    viewModel: HomeViewModel,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.padding_zero)),
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentQueries by viewModel.recentQueries.collectAsState()
    val newsItems = viewModel.homeNewsPagingFlow.collectAsLazyPagingItems()

    HomeNewsLazyPagingList(
        newsItems = newsItems,
        searchQuery = searchQuery,
        recentQueries = recentQueries,
        onSearchTextChange = {
            viewModel.onSearchTextChanged(it)
        },
        onSearchTriggered = {
            viewModel.run { onSearchTriggered(it) }
        },
        onUseRecentQuery = {
            viewModel.onSearchTextChanged(it)
            viewModel.onSearchTriggered(it)
        },
        onArticleClicked = onArticleClicked,
        onBookmarkClick = { url, isSaved ->
            viewModel.toggleBookmark(url, isSaved)
        },
        modifier = modifier,
        contentPadding = contentPadding
    )
}

@Composable
fun HomeNewsLazyPagingList(
    newsItems: LazyPagingItems<Article>,
    searchQuery: String,
    recentQueries: List<String>,
    onArticleClicked: (String) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onUseRecentQuery: (String) -> Unit,
    onBookmarkClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.padding_zero))
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .then(modifier)
    ) {
        OutlinedTextFieldHomeScreen(
            searchQuery = searchQuery,
            onSearchTextChange = onSearchTextChange,
            onSearchTriggered = onSearchTriggered,
            recentQueries = recentQueries,
            onUseRecentQuery = onUseRecentQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_large),
                    vertical = dimensionResource(id = R.dimen.padding_default)
                )
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = dimensionResource(id = R.dimen.padding_large))
        ) {
            // ПЕРЕВІРКА НА СТАН ЗАВАНТАЖЕННЯ (SKELETON)
            if (newsItems.loadState.refresh is LoadState.Loading) {
                items(5) {
                    NewsCardSkeleton()
                }
            } else {
                // РЕАЛЬНІ ДАНІ
                items(newsItems.itemCount) { index ->
                    val article = newsItems[index]
                    article?.let {
                        NewsCard(
                            article = it,
                            isSaved = it.isSaved,
                            onBookmarkClick = {
                                onBookmarkClick(it.url, !it.isSaved)
                            },
                            onClick = {
                                val url = it.url
                                onArticleClicked(url)
                            }
                        )
                    }
                }
            }

            newsItems.apply {
                item {
                    if (loadState.refresh is LoadState.Error) {
                        PagingLoadStateHandler(loadState = loadState.refresh, retry = { retry() })
                    }
                }
                item {
                    // Обробка дозавантаження (скролінг вниз)
                    PagingLoadStateHandler(loadState = loadState.append, retry = { retry() })
                }
            }
        }
    }
}