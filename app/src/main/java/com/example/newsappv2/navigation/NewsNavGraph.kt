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
import com.example.newsappv2.ui.screens.SavedArticlesDestination
import com.example.newsappv2.ui.screens.SavedArticlesScreen
import com.example.newsappv2.ui.screens.SettingsDestination
import com.example.newsappv2.ui.screens.SettingsScreen
import com.example.newsappv2.viewmodel.CategoryViewModel
import com.example.newsappv2.viewmodel.HomeViewModel
import com.example.newsappv2.viewmodel.SavedArticlesViewModel
import com.example.newsappv2.ui.screens.OnboardingDestination
import com.example.newsappv2.ui.screens.OnboardingScreen

@Composable
fun NewsNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = OnboardingDestination.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(HomeDestination.route) {
                        popUpTo(OnboardingDestination.route) { inclusive = true }
                    }
                }
            )
        }
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

        composable(route = SavedArticlesDestination.route) {
            val viewModelSaved: SavedArticlesViewModel = hiltViewModel()
            SavedArticlesScreen(
                viewModel = viewModelSaved,
                contentPadding = contentPadding,
                onArticleClicked = { url ->
                    val encoded = Uri.encode(url)
                    navController.navigate("webview/$encoded")
                }
            )
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