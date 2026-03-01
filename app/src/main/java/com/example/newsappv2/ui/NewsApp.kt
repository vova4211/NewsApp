package com.example.newsappv2.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.newsappv2.ui.screens.RootScreen


@Composable
fun NewsApp(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    RootScreen(
        navController = navController,
        modifier = modifier
    )
}
