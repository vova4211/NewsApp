package com.example.newsappv2.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.newsappv2.ui.screens.CategoriesDestination
import com.example.newsappv2.ui.screens.CategoriesScreen
import com.example.newsappv2.ui.screens.HomeDestination
import com.example.newsappv2.ui.screens.HomeScreen
import com.example.newsappv2.viewmodel.AppViewModelProvider
import com.example.newsappv2.viewmodel.CategoryViewModel
import com.example.newsappv2.viewmodel.HomeViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModelHome: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    viewModelCategory: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val selectCategory by viewModelCategory.selectCategory.collectAsState()
    val searchQuery by viewModelHome.searchQuery.collectAsState()
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                retryAction = { viewModelHome.onSearchTextChanged(searchQuery) },
                viewModel = viewModelHome,
                contentPadding = contentPadding,
                navigateToCategories = { navController.navigate(CategoriesDestination.route) }
            )
        }
        composable(route = CategoriesDestination.route) {
            CategoriesScreen(
                navigateBack = { navController.popBackStack() },
                viewModel = viewModelCategory,
                retryAction = { viewModelCategory.onCategorySelected(viewModelCategory.selectCategory.value)},
                contentPadding = contentPadding,
            )
        }
    }
}