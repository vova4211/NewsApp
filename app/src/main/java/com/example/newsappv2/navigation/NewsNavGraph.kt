package com.example.newsappv2.navigation

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.newsappv2.ui.screens.ArticleDetailsScreen
import com.example.newsappv2.ui.screens.CategoriesDestination
import com.example.newsappv2.ui.screens.CategoriesScreen
import com.example.newsappv2.ui.screens.HomeDestination
import com.example.newsappv2.ui.screens.HomeScreen
import com.example.newsappv2.ui.screens.SettingsDestination
import com.example.newsappv2.ui.screens.SettingsScreen
import com.example.newsappv2.viewmodel.CategoryViewModel
import com.example.newsappv2.viewmodel.HomeViewModel

@Composable
fun NewsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            val viewModelHome: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModelHome,
                contentPadding = contentPadding,
                onArticleClicked = { url ->
                    val encoded = Uri.encode(url)
                    navController.navigate("webview/$encoded")
                }
            )
        }
        composable(route = CategoriesDestination.route) {
            val viewModelCategory: CategoryViewModel = hiltViewModel()
            CategoriesScreen(
                viewModel = viewModelCategory,
                contentPadding = contentPadding,
                onArticleClicked = { url ->
                    val encoded = Uri.encode(url)
                    navController.navigate("webview/$encoded")
                }
            )
        }
        composable(route = SettingsDestination.route) {
            SettingsScreen()
        }
        composable(
            route = "webview/{encodedUrl}",
            arguments = listOf(navArgument("encodedUrl") { type = NavType.StringType })
        ){
            ArticleDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }
}