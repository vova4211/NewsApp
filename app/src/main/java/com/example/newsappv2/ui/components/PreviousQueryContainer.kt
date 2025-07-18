package com.example.newsappv2.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.newsappv2.R


@Composable
fun PreviousQueryContainer(
    lastQuery: String,
    onUseLastQuery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = dimensionResource(id = R.dimen.previous_query_border),
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.previous_query_corner_shape))
            )
            .clickable { onUseLastQuery(lastQuery) }
            .padding(horizontal = dimensionResource(id = R.dimen.padding_large), vertical = dimensionResource(id = R.dimen.padding_medium)),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = lastQuery,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 1f)
        )
    }
}
