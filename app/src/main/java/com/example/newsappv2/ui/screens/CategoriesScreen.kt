package com.example.newsappv2.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
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
import com.example.newsappv2.viewmodel.AppViewModelProvider
import com.example.newsappv2.viewmodel.CategoryViewModel


object CategoriesDestination: NavigationDestination {
    override val route = "categories"
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CategoriesScreen(
    navigateBack: () -> Unit,
    viewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    retryAction:() -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
//    val categoryNewsUiState by viewModel.categoryNewsUiState.collectAsState()

    val newsCategoryItems = viewModel.newsCategoryPagingFlow.collectAsLazyPagingItems()

//    when(categoryNewsUiState) {
//        is NewsUiState.Loading -> LoadingScreen(modifier.fillMaxSize())
//
//        is NewsUiState.Success -> CategoryNewsLazyColumn(
//            news = (categoryNewsUiState as NewsUiState.Success).news,
//            contentPadding = contentPadding
//        )
//
//        is NewsUiState.Error -> ErrorScreen(retryAction, modifier.fillMaxSize())
//
//    }

    CategoryNewsLazyPagingList(
        newsCategoryItems = newsCategoryItems,
        modifier = modifier,
        contentPadding =contentPadding
    )

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CategoryNewsLazyPagingList(
    newsCategoryItems: LazyPagingItems<Article>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {

        items(newsCategoryItems.itemCount) { index ->
            val article = newsCategoryItems[index]
            article?.let {
                NewsCard(
                    article = it
                )
            }
        }

        newsCategoryItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { LoadingScreen(modifier.fillMaxSize()) }
                }
                loadState.refresh is LoadState.Error -> {
                    item{
                        val e = loadState.refresh as LoadState.Error
                        ErrorScreen(
                            retryAction = {retry()},
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
                        ErrorScreen(retryAction = {retry()},
                            modifier = Modifier.fillMaxSize())
                    }
                }
            }

        }
    }
}




//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun CategoryNewsLazyColumn(
//    news: NewsResponse,
//    modifier: Modifier =  Modifier,
//    contentPadding: PaddingValues = PaddingValues(0.dp)
//) {
//    val articles = news.articles
//    LazyColumn(
//        modifier = modifier,
//        contentPadding = contentPadding) {
//        items(articles) { article ->
//            NewsCard(article = article)
//
//        }
//    }
//}
