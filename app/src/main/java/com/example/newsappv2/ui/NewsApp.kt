package com.example.newsappv2.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.newsappv2.ui.screens.HomeDestination
import com.example.newsappv2.ui.screens.OnboardingDestination
import com.example.newsappv2.ui.screens.RootScreen
import com.example.newsappv2.ui.theme.NewsAppV2Theme
import com.example.newsappv2.util.ThemeMode
import com.example.newsappv2.viewmodel.MainViewModel

@Composable
fun NewsApp(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val themeMode by mainViewModel.themeMode.collectAsState()
    val isFirstLaunch by mainViewModel.isFirstLaunch.collectAsState()

    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    NewsAppV2Theme(darkTheme = isDarkTheme) {
        if (isFirstLaunch == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val startRoute = if (isFirstLaunch == true) {
                OnboardingDestination.route
            } else {
                HomeDestination.route
            }

            val navController = rememberNavController()
            RootScreen(
                navController = navController,
                startDestination = startRoute,
                modifier = modifier
            )
        }
    }
}