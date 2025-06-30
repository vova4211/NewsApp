package com.example.newsappv2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.newsappv2.R
import com.example.newsappv2.ui.screens.CategoriesDestination
import com.example.newsappv2.ui.screens.HomeDestination

@Composable
fun NewsBottomBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {

    val homeSelected = currentRoute == HomeDestination.route
    val categoriesSelected = currentRoute == CategoriesDestination.route


    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        tonalElevation = 8.dp,
        actions = {
            IconButton(
                onClick = { onTabSelected(HomeDestination.route) },
            ) {
                BottomBarItem(
                    icon = Icons.Filled.Home,
                    text = stringResource(R.string.home),
                    selected = homeSelected
                )
            }
            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = { onTabSelected(CategoriesDestination.route) },
            ) {
                BottomBarItem(
                    icon = Icons.Filled.Menu,
                    text = stringResource(R.string.categories),
                    selected = categoriesSelected
                )
            }
        }
    )
}

@Composable
fun BottomBarItem(icon: ImageVector, text: String, selected: Boolean) {

    val backgroundColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
    else Color.Transparent

    val iconTint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
    else  MaterialTheme.colorScheme.outlineVariant

    val textColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer}
    else {
        MaterialTheme.colorScheme.outlineVariant}

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(8.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
