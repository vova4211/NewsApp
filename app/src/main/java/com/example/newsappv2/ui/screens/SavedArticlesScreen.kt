package com.example.newsappv2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import com.example.newsappv2.R
import com.example.newsappv2.navigation.NavigationDestination
import com.example.newsappv2.ui.components.NewsCard
import com.example.newsappv2.viewmodel.SavedArticlesViewModel

object SavedArticlesDestination : NavigationDestination {
    override val route = "saved"
}

@Composable
fun SavedArticlesScreen(
    onArticleClicked: (String) -> Unit,
    viewModel: SavedArticlesViewModel,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.padding_zero)),
    modifier: Modifier = Modifier
) {
    val savedArticles by viewModel.savedArticles.collectAsState()

    if (savedArticles.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "📂",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "Немає збережених статей",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_default))
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding
        ) {
            items(
                items = savedArticles,
                key = { article -> article.url }
            ) { article ->
                NewsCard(
                    article = article,
                    isSaved = article.isSaved,
                    onBookmarkClick = {
                        viewModel.toggleBookmark(article.url, !article.isSaved)
                    },
                    onClick = {
                        onArticleClicked(article.url)
                    }
                )
            }
        }
    }
}