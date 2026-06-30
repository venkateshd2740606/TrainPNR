package com.trainpnr.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.trainpnr.ads.AdManager
import com.trainpnr.analytics.AnalyticsManager
import com.trainpnr.domain.model.UserPreferences
import com.trainpnr.presentation.ui.screens.pnr.*

sealed class Screen(val route: String) {
    data object CheckPnr : Screen("check_pnr")
    data object SavedList : Screen("saved_list")
    data object Guide : Screen("guide")
    data object Settings : Screen("settings")
    data object Status : Screen("status")
}

@Composable
fun TrainPNRNavHost(
    navController: NavHostController,
    adManager: AdManager,
    analyticsManager: AnalyticsManager,
    preferences: UserPreferences,
    startDestination: String = Screen.CheckPnr.route
) {
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route
    val tabs = listOf(
        Triple(Screen.CheckPnr.route, Icons.Default.Search, "Check"),
        Triple(Screen.SavedList.route, Icons.Default.List, "Saved"),
        Triple(Screen.Guide.route, Icons.Default.Book, "Guide"),
        Triple(Screen.Settings.route, Icons.Default.Settings, "Settings")
    )

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Status.route) {
                NavigationBar {
                    tabs.forEach { (route, icon, label) ->
                        NavigationBarItem(
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination, Modifier.padding(padding)) {
            composable(Screen.CheckPnr.route) {
                CheckPnrScreen(onShowStatus = { navController.navigate(Screen.Status.route) })
            }
            composable(Screen.Status.route) {
                PnrStatusScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.SavedList.route) {
                SavedPnrScreen(onOpenPnr = { pnr ->
                    navController.navigate(Screen.CheckPnr.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    }
                })
            }
            composable(Screen.Guide.route) { PnrGuideScreen() }
            composable(Screen.Settings.route) { PnrSettingsScreen() }
        }
    }
}
