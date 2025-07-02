package com.example.newsappv2.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.newsappv2.R
import com.example.newsappv2.data.model.Article
import com.example.newsappv2.data.model.Source
import com.example.newsappv2.ui.theme.NewsAppV2Theme
import com.example.newsappv2.util.formatAsDate

@Composable
fun NewsCard(
    modifier: Modifier = Modifier,
    article: Article,
) {
    val urlToImageText = article.urlToImage?.replace("http://", "https://")
    val urlText = article.url?.replace("http://", "https://")
    val authorText = article.author ?: stringResource(R.string.author_unknown)
    val titleText = article.title ?: stringResource(R.string.title_unknown)
    val descriptionText = article.description ?: stringResource(R.string.unknown_description)
    val nameText = article.source.name ?: stringResource(R.string.unknown_source)
    val publishedAtText = article.publishedAt ?: stringResource(R.string.unknown_when_published)
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(!urlToImageText.isNullOrBlank()) }


    Card(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_default))
            .fillMaxWidth(),
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
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_medium)))
            Box( modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(topStart = dimensionResource(id = R.dimen.box_corner_radius), topEnd = dimensionResource(id = R.dimen.box_corner_radius)))
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
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_small)))
            Text(
                text = stringResource(R.string.read_more),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlText))
                        context.startActivity(intent)
                    },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textDecoration = TextDecoration.Underline
            )

        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewsCardPreview(
) {
    NewsAppV2Theme{
        val mockData =
            Article(
                source = Source(
                    id = null,
                    name = "BBC News"
                ),
                author = "BBC News",
                title = "Small boats situation getting worse, says No 10",
                url = "https://www.bbc.co.uk/news/articles/c39zk7pp29ko",
                urlToImage = "https://ichef.bbci.co.uk/ace/branded_news/1200/cpsprodpb/881b/live/fd0f5960-4b6f-11f0-a6b4-f7f4e17c182c.jpg",
                publishedAt = "2025-06-17T14:22:18.6035575Z",
                description = "Downing Street said Sir Keir Starmer would make the issue the key focus of a summit with France next month."
            )

        NewsCard(
            article = mockData,
            modifier = Modifier
        )
    }
}
