package com.example.newsappv2.ui.components

import com.example.newsappv2.R
import com.example.newsappv2.viewmodel.AppViewModelProvider
import com.example.newsappv2.viewmodel.CategoryViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesTopBarWithDrawerButton(
    onDrawerClick: () -> Unit,
    viewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier

) {
    val selectedCategory by viewModel.selectCategory.collectAsState()

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDrawerClick
                ) {
                    Icon(
                        Icons.Filled.Menu,
                        contentDescription = stringResource(R.string.menu_drawer_icon)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedCategory.categoryName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    )
}
