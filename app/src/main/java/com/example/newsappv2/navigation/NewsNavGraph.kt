package com.example.newsappv2.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
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


@Composable
fun NewsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModelHome: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    viewModelCategory: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                viewModel = viewModelHome,
                contentPadding = contentPadding
            )
        }
        composable(route = CategoriesDestination.route) {
            CategoriesScreen(
                viewModel = viewModelCategory,
                contentPadding = contentPadding,
            )
        }
    }
}