package com.example.newsappv2.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newsappv2.util.Category
import com.example.newsappv2.viewmodel.AppViewModelProvider
import com.example.newsappv2.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
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
            ModalDrawerSheet {
                Category.entries.forEach { category ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = category.categoryImage),
                                contentDescription = category.categoryName
                            )
                        },
                        label = {
                            Text(text = category.categoryName)
                        },
                        selected = category == selectedCategory,
                        onClick = {
                            viewModel.onCategorySelected(category)
                            viewModel.persistLastSelectedCategory(category)
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        },
        content = content

    )
}


