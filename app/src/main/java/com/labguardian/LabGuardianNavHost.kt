package com.labguardian

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.labguardian.core.ui.theme.LabGuardianTheme
import com.labguardian.feature.camera.CameraScreen
import com.labguardian.feature.dashboard.DashboardScreen
import com.labguardian.feature.guidance.GuidanceScreen

enum class TopRoute(val route: String, val label: String, val icon: ImageVector) {
    DASHBOARD("dashboard", "Dashboard", Icons.Default.Dashboard),
    CAMERA("camera", "Camera", Icons.Default.CameraAlt),
    GUIDANCE("guidance", "Guidance", Icons.AutoMirrored.Filled.Message),
}

@Composable
fun LabGuardianNavHost() {
    val navController = rememberNavController()

    LabGuardianTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    TopRoute.entries.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = TopRoute.DASHBOARD.route,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(TopRoute.DASHBOARD.route) { DashboardScreen() }
                composable(TopRoute.CAMERA.route) { CameraScreen() }
                composable(TopRoute.GUIDANCE.route) { GuidanceScreen() }
            }
        }
    }
}
