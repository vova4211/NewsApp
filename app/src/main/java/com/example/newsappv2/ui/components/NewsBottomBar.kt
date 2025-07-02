package com.example.newsappv2.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.newsappv2.R
import com.example.newsappv2.ui.screens.CategoriesDestination
import com.example.newsappv2.ui.screens.HomeDestination
import com.example.newsappv2.ui.theme.NewsAppV2Theme
import com.example.newsappv2.util.currentRoute

@Composable
fun NewsBottomBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    val homeSelected = currentRoute == HomeDestination.route
    val categoriesSelected = currentRoute == CategoriesDestination.route

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.padding_medium), vertical = dimensionResource(id = R.dimen.padding_default)),
        tonalElevation = dimensionResource(id = R.dimen.bottom_bar_total_elevation),
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_medium))
            ) {
                val homeBackgroundColor by animateColorAsState(
                    targetValue = if (homeSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
                )
                val homeBorderColor by animateColorAsState(
                    targetValue = if (homeSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(dimensionResource(id  = R.dimen.box_corner_radius)))
                        .background(homeBackgroundColor)
                        .border(dimensionResource(id = R.dimen.bottom_bar_box_border), homeBorderColor, RoundedCornerShape(dimensionResource(id  = R.dimen.box_corner_radius)))
                        .clickable { onTabSelected(HomeDestination.route) }
                        .padding(vertical = dimensionResource(id = R.dimen.padding_medium), horizontal = dimensionResource(id = R.dimen.padding_large))
                ) {
                    BottomBarItem(
                        icon = Icons.Filled.Home,
                        text = stringResource(R.string.home),
                        selected = homeSelected,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                val categoriesBackgroundColor by animateColorAsState(
                    targetValue = if (categoriesSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
                )
                val categoriesBorderColor by animateColorAsState(
                    targetValue = if (categoriesSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(dimensionResource(id  = R.dimen.box_corner_radius)))
                        .background(categoriesBackgroundColor)
                        .border(dimensionResource(id = R.dimen.bottom_bar_box_border), categoriesBorderColor, RoundedCornerShape(dimensionResource(id  = R.dimen.box_corner_radius)))
                        .clickable { onTabSelected(CategoriesDestination.route) }
                        .padding(vertical = dimensionResource(id = R.dimen.padding_medium), horizontal = dimensionResource(id = R.dimen.padding_large))
                ) {
                    BottomBarItem(
                        icon = Icons.Filled.Menu,
                        text = stringResource(R.string.categories),
                        selected = categoriesSelected,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}

@Composable
fun BottomBarItem(
    icon: ImageVector,
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val iconTint by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outlineVariant
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.size(dimensionResource(id =  R.dimen.bottom_bar_icon_size))
        )
    }
}



@Preview
@Composable
fun NewsBottomBarPreview() {
    NewsAppV2Theme {
        val navController = rememberNavController()
        val currentRoute = currentRoute(navController)
        NewsBottomBar(
            currentRoute = currentRoute
        ) {}
    }
}
