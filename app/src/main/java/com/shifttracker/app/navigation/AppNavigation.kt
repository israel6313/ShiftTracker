package com.shifttracker.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shifttracker.app.ui.screen.*
import com.shifttracker.app.ui.theme.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "ראשי", Icons.Default.Home)
    object Shifts : Screen("shifts", "משמרות", Icons.Default.List)
    object Jobs : Screen("jobs", "עבודות", Icons.Default.Work)
    object Settings : Screen("settings", "הגדרות", Icons.Default.Settings)
}

@Composable
fun AppNavigation(userName: String) {
    val navController = rememberNavController()
    var showAddMenu by remember { mutableStateOf(false) }

    val bottomNavItems = listOf(
        Screen.Settings,
        Screen.Jobs,
        null, // Center add button placeholder
        Screen.Shifts,
        Screen.Dashboard
    )

    Scaffold(
        containerColor = Background,
        bottomBar = {
            NavigationBar(
                containerColor = Surface,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { screen ->
                    if (screen == null) {
                        // Center add button
                        NavigationBarItem(
                            icon = {
                                Surface(
                                    color = Primary,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "הוסף",
                                        tint = OnPrimary,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            },
                            label = { Text("הוסף", color = TextSecondary) },
                            selected = false,
                            onClick = { showAddMenu = true },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Surface
                            )
                        )
                    } else {
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(screen.icon, screen.label) },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = SurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(userName = userName) }
            composable(Screen.Shifts.route) { ShiftsScreen() }
            composable(Screen.Jobs.route) { JobsScreen() }
            composable(Screen.Settings.route) { SettingsScreen(userName = userName) }
        }
    }

    if (showAddMenu) {
        AddMenuSheet(onDismiss = { showAddMenu = false })
    }
}
