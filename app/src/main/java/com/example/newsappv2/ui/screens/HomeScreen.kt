package com.example.newsappv2.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.newsappv2.data.model.Article
import com.example.newsappv2.navigation.NavigationDestination
import com.example.newsappv2.ui.components.ErrorScreen
import com.example.newsappv2.ui.components.LoadingScreen
import com.example.newsappv2.ui.components.NewsCard
import com.example.newsappv2.ui.components.OutlinedTextFieldHomeScreen
import com.example.newsappv2.viewmodel.AppViewModelProvider
import com.example.newsappv2.viewmodel.HomeViewModel


object HomeDestination: NavigationDestination {
    override val route = "home"
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navigateToCategories: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    retryAction:() -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lastQuery by viewModel.lastSearchQuery.collectAsState()
//    val homeNewsUiState by viewModel.homeNewsUiState.collectAsState()

    val newsItems = viewModel.homeNewsPagingFlow.collectAsLazyPagingItems()


//    when(homeNewsUiState) {
//        is NewsUiState.Loading -> LoadingScreen(modifier.fillMaxSize())
//
//        is NewsUiState.Success -> HomeNewsLazyColumn(
//           news = (homeNewsUiState as NewsUiState.Success).news,
//            searchQuery = searchQuery,
//            onSearchTextChange = viewModel::onSearchTextChanged,
//            contentPadding = contentPadding
//        )
//        is NewsUiState.Error -> ErrorScreen(retryAction, modifier.fillMaxSize())
//
//    }

    HomeNewsLazyPagingList(
        newsItems =  newsItems,
        searchQuery = searchQuery,
        lastQuery = lastQuery,
        onSearchTextChange = {
            viewModel.onSearchTextChanged(it)
            viewModel.persistLastSearchQuery(it)
        },
        onUseLastQuery = { viewModel.onSearchTextChanged(it)},
        modifier = modifier,
        contentPadding =contentPadding
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeNewsLazyPagingList(
    newsItems: LazyPagingItems<Article>,
    searchQuery: String,
    lastQuery: String,
    onSearchTextChange: (String) -> Unit,
    onUseLastQuery: (String) -> Unit,
    modifier: Modifier =  Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(newsItems.itemCount) { index ->
                val article = newsItems[index]
                article?.let {
                    NewsCard(
                        article = it
                    )
                }
            }

            newsItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { LoadingScreen(modifier.fillMaxSize()) }
                    }

                    loadState.refresh is LoadState.Error -> {
                        item {
                            val e = loadState.refresh as LoadState.Error
                            ErrorScreen(
                                retryAction = { retry() },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    loadState.append is LoadState.Loading -> {
                        item { LoadingScreen(modifier.fillMaxSize()) }
                    }

                    loadState.append is LoadState.Error -> {
                        item {
                            val e = loadState.append as LoadState.Error
                            ErrorScreen(
                                retryAction = { retry() },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun HomeNewsLazyColumn(
//    news: NewsResponse,
//    searchQuery: String,
//    onSearchTextChange: (String) -> Unit,
//    modifier: Modifier =  Modifier,
//    contentPadding: PaddingValues = PaddingValues(0.dp)
//) {
//    val articles = news.articles
//
//    LazyColumn(
//        modifier = modifier,
//        contentPadding = contentPadding) {
//        item {
//            OutlinedTextField(
//                value = searchQuery,
//                onValueChange = onSearchTextChange,
//                placeholder = { Text(stringResource(R.string.enter_a_new_query)) },
//                leadingIcon = {
//                    Icon(
//                        Icons.Filled.Search,
//                        contentDescription = stringResource(R.string.search_icon)
//                    )
//                },
//                trailingIcon = {
//                    IconButton(onClick = { onSearchTextChange("") }) {
//                        Icon(
//                            imageVector = Icons.Filled.Clear,
//                            contentDescription = stringResource(R.string.clear_text)
//                        )
//                    }
//                },
//                enabled = true,
//                singleLine = true,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = MaterialTheme.colorScheme.surface,
//                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
//                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
//                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
//                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            )
//        }
//        items(articles) { article ->
//            NewsCard(article = article)
//        }
//    }
//}
//
//





