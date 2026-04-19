package com.example.newsappv2.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.newsappv2.util.PagingLoadStateHandler
import com.example.newsappv2.viewmodel.CategoryViewModel

object CategoriesDestination: NavigationDestination {
    override val route = "categories"
}

@Composable
fun CategoriesScreen(
    onArticleClicked: (String) -> Unit,
    viewModel: CategoryViewModel,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.padding_zero)),
    modifier: Modifier = Modifier
) {
    val newsCategoryItems = viewModel.newsCategoryPagingFlow.collectAsLazyPagingItems()
    val targetLanguage by viewModel.targetLanguage.collectAsState()

    LaunchedEffect(targetLanguage) {
        if (targetLanguage.isNotEmpty()) {
            newsCategoryItems.refresh()
        }
    }

    CategoryNewsLazyPagingList(
        newsCategoryItems = newsCategoryItems,
        onArticleClicked = onArticleClicked,
        onBookmarkClick = { url, isSaved ->
            viewModel.toggleBookmark(url, isSaved)
        },
        modifier = modifier,
        contentPadding = contentPadding
    )
}

@Composable
fun CategoryNewsLazyPagingList(
    newsCategoryItems: LazyPagingItems<Article>,
    onBookmarkClick: (String, Boolean) -> Unit,
    onArticleClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.padding_zero))
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        if (newsCategoryItems.loadState.refresh is LoadState.Loading) {
            items(5) {
                NewsCardSkeleton()
            }
        } else {
            // РЕАЛЬНІ ДАНІ
            items(newsCategoryItems.itemCount) { index ->
                val article = newsCategoryItems[index]
                article?.let {
                    NewsCard(
                        article = it,
                        isSaved = it.isSaved,
                        onBookmarkClick = {
                            onBookmarkClick(it.url, !it.isSaved)
                        },
                        onClick = { onArticleClicked(it.url) },
                    )
                }
            }
        }

        newsCategoryItems.apply {
            item {
                // Показуємо помилку (якщо є) при першому завантаженні
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