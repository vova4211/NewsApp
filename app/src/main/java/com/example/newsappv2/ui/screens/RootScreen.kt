package com.example.newsappv2.ui.screens

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.newsappv2.navigation.NewsNavHost
import com.example.newsappv2.ui.components.CategoriesTopBarWithDrawerButton
import com.example.newsappv2.ui.components.DrawerContainerForCategories
import com.example.newsappv2.ui.components.HomeTopBar
import com.example.newsappv2.ui.components.NewsBottomBar
import com.example.newsappv2.util.currentRoute
import com.example.newsappv2.viewmodel.AppViewModelProvider
import com.example.newsappv2.viewmodel.CategoryViewModel
import com.example.newsappv2.viewmodel.HomeViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelHome: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    viewModelCategory: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {
    val currentRoute = currentRoute(navController)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val onTabSelected: (String) -> Unit = { route ->
        if (route != currentRoute) {
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }


    val homeScrollBehavior  = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val categoriesScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    if (currentRoute == CategoriesDestination.route) {
        DrawerContainerForCategories(
            viewModel = viewModelCategory,
            drawerState = drawerState,
            content = {
                Scaffold(
                    topBar = {
                        CategoriesTopBarWithDrawerButton(
                            viewModel = viewModelCategory,
                            scrollBehavior = categoriesScrollBehavior,
                            onDrawerClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NewsBottomBar(
                            currentRoute = currentRoute,
                            onTabSelected = onTabSelected
                        )
                    },
                    modifier = modifier
                ) { innerPadding ->
                    NewsNavHost(
                        viewModelCategory = viewModelCategory,
                        viewModelHome = viewModelHome,
                        contentPadding = innerPadding,
                        navController = navController
                    )
                }
            }
        )
    } else {
        Scaffold(
            topBar = { HomeTopBar(
                scrollBehavior = homeScrollBehavior
            ) },
            bottomBar = {
                NewsBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = onTabSelected
                )
            },
            modifier = Modifier.nestedScroll(homeScrollBehavior.nestedScrollConnection)

        ) { innerPadding ->
            NewsNavHost(
                viewModelCategory = viewModelCategory,
                viewModelHome = viewModelHome,
                contentPadding = innerPadding,
                navController = navController
            )

        }
    }
}
