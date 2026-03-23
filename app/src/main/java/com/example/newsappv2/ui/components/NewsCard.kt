package com.example.newsappv2.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.newsappv2.domain.model.Article
import com.example.newsappv2.R
import com.example.newsappv2.ui.theme.NewsAppV2Theme
import com.example.newsappv2.util.formatAsDate

@Composable
fun NewsCard(
    modifier: Modifier = Modifier,
    article: Article,
    // ДОДАЛИ НОВІ ПАРАМЕТРИ ДЛЯ ЗАКЛАДКИ
    isSaved: Boolean = false,
    onBookmarkClick: () -> Unit = {},
    onClick: () -> Unit,
) {
    val urlToImageText = article.urlToImage.replace("http://", "https://")
    val authorText = article.author
    val titleText = article.title
    val descriptionText = article.description
    val nameText = article.sourceName
    val publishedAtText = article.publishedAt
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(urlToImageText.isNotBlank()) }

    Card(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_default))
            .fillMaxWidth()
            .clickable { onClick() }, // Перенесли onClick на всю картку для зручності
        border = BorderStroke(dimensionResource(id = R.dimen.card_border_width), MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.card_elevation))
    ) {

        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_default)),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Зберегти статтю",
                        tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_medium)))

            Box( modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(
                    RoundedCornerShape(
                        topStart = dimensionResource(id = R.dimen.box_corner_radius),
                        topEnd = dimensionResource(id = R.dimen.box_corner_radius)
                    )
                )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(urlToImageText)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.missing_image),
                    contentScale = ContentScale.FillWidth,
                    error = painterResource(R.drawable.no_data_amico),
                    onSuccess = { isLoading = false},
                    onError = { isLoading = false},
                    onLoading = { isLoading = true},
                    modifier = Modifier.fillMaxSize()
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(dimensionResource(id = R.dimen.image_circular_loader))
                    )
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_medium)))
            Text(
                text = stringResource(R.string.author) + authorText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.publishing_house) + nameText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_small)))
            Text(
                text = publishedAtText.formatAsDate(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_small)))
            Text(
                text = descriptionText,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}