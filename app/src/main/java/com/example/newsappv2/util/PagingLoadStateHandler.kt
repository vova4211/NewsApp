package com.example.newsappv2.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import com.example.newsappv2.ui.components.ErrorScreen
import com.example.newsappv2.ui.components.LoadingScreen

@Composable
fun PagingLoadStateHandler(
    loadState: LoadState,
    retry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (loadState) {
        is LoadState.Loading -> LoadingScreen(modifier.fillMaxSize())
        is LoadState.Error -> ErrorScreen(retryAction = retry, modifier = modifier.fillMaxSize())
        else -> {}
    }
}
