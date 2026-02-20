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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.newsappv2.R
import com.example.newsappv2.data.model.Article
import com.example.newsappv2.navigation.NavigationDestination
import com.example.newsappv2.ui.components.NewsCard
import com.example.newsappv2.ui.components.OutlinedTextFieldHomeScreen
import com.example.newsappv2.util.PagingLoadStateHandler
import com.example.newsappv2.viewmodel.AppViewModelProvider
import com.example.newsappv2.viewmodel.HomeViewModel
import java.net.URLEncoder


object HomeDestination: NavigationDestination {
    override val route = "home"
}


@Composable
fun HomeScreen(
    onArticleClicked: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.padding_zero)),
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lastQuery by viewModel.lastSearchQuery.collectAsState()

    val newsItems = viewModel.homeNewsPagingFlow.collectAsLazyPagingItems()

    HomeNewsLazyPagingList(
        newsItems =  newsItems,
        searchQuery = searchQuery,
        lastQuery = lastQuery,
        onSearchTextChange = {
            viewModel.onSearchTextChanged(it)
            viewModel.persistLastSearchQuery(it)
        },
        onUseLastQuery = { viewModel.onSearchTextChanged(it)},
        onArticleClicked = onArticleClicked,
        modifier = modifier,
        contentPadding =contentPadding
    )
}

@Composable
fun HomeNewsLazyPagingList(
    newsItems: LazyPagingItems<Article>,
    searchQuery: String,
    lastQuery: String,
    onArticleClicked: (String) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onUseLastQuery: (String) -> Unit,
    modifier: Modifier =  Modifier,
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
            lastQuery = lastQuery,
            onUseLastQuery = onUseLastQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_large), vertical = dimensionResource(id = R.dimen.padding_default))
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = dimensionResource(id = R.dimen.padding_large))
        ) {
            items(newsItems.itemCount) { index ->
                val article = newsItems[index]
                article?.let {
                    NewsCard(
                        article = it,
                        onClick = {
                            val url = it.url ?: return@NewsCard
                            onArticleClicked(url)
                        }
                    )
                }
            }
            newsItems.apply {
                item {
                    PagingLoadStateHandler(loadState = loadState.refresh, retry = { retry() })
                }
                item {
                    PagingLoadStateHandler(loadState = loadState.append, retry = { retry() })
                }
            }
        }
    }
}




