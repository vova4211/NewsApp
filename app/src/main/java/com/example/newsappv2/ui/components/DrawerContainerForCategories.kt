package com.example.newsappv2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newsappv2.R
import com.example.newsappv2.util.Category
import com.example.newsappv2.viewmodel.AppViewModelProvider
import com.example.newsappv2.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch


@Composable
fun DrawerContainerForCategories(
    viewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    content: @Composable () -> Unit
) {
    val selectedCategory by viewModel.selectCategory.collectAsState()
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.widthIn(max = dimensionResource(id = R.dimen.drawer_max_width))
            ) {
                Category.entries.forEach { category ->
                    val selected = category == selectedCategory
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimensionResource(id = R.dimen.padding_small), horizontal = dimensionResource(id = R.dimen.padding_default))
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.drawer_corner_shape)))
                            .background(
                                if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            )
                            .border(
                                dimensionResource(id = R.dimen.drawer_border),
                                if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                RoundedCornerShape(dimensionResource(id = R.dimen.drawer_corner_shape))
                            )
                            .clickable {
                                viewModel.onCategorySelected(category)
                                viewModel.persistLastSelectedCategory(category)
                                scope.launch { drawerState.close() }
                            }
                            .padding(dimensionResource(id = R.dimen.padding_medium)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = category.categoryImage),
                            contentDescription = category.categoryName,
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.image_icon_size))
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.drawer_spacer)))
                        Text(
                            text = category.categoryName,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        content = content
    )
}


