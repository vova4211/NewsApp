package com.example.newsappv2.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.newsappv2.ui.screens.RootScreen
import com.example.newsappv2.viewmodel.AppViewModelProvider
import com.example.newsappv2.viewmodel.CategoryViewModel
import com.example.newsappv2.viewmodel.HomeViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsApp(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val viewModelHome: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val viewModelCategory: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
    RootScreen(
        navController = navController,
        viewModelCategory = viewModelCategory,
        viewModelHome = viewModelHome,
        modifier = modifier
    )
}
