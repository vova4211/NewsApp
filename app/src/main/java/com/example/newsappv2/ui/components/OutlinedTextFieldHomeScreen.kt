package com.example.newsappv2.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.newsappv2.R


@Composable
fun OutlinedTextFieldHomeScreen(
    searchQuery: String,
    lastQuery: String,
    onSearchTextChange: (String) -> Unit,
    onUseLastQuery: (String) -> Unit,
    modifier: Modifier =  Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchTextChange,
            placeholder = { Text(stringResource(R.string.enter_a_new_query)) },
            trailingIcon = {
                IconButton(onClick = { onSearchTextChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.clear_text),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            enabled = true,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.outlined_text_field_height))
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        AnimatedVisibility(visible = isFocused && lastQuery.isNotBlank() && lastQuery != searchQuery) {
            PreviousQueryContainer(
                lastQuery = lastQuery,
                onUseLastQuery = onUseLastQuery,
                modifier = Modifier.padding(top = dimensionResource( id = R.dimen.padding_default), start = dimensionResource( id = R.dimen.padding_large), end = dimensionResource( id = R.dimen.padding_large))
            )
        }
    }
}