package com.example.autoservice.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.autoservice.R
import com.example.autoservice.data.AppDatabase
import com.example.autoservice.data.AutoRepository
import com.example.autoservice.data.ReminderRepository
import com.example.autoservice.data.RequestItemRepository
import com.example.autoservice.data.RequestRepository
import com.example.autoservice.ui.screens.analytics.AnalyticsScreen
import com.example.autoservice.ui.screens.analytics.AnalyticsViewModel
import com.example.autoservice.ui.screens.auth.LoginScreen
import com.example.autoservice.ui.screens.dashboard.DashboardScreen
import com.example.autoservice.ui.screens.dashboard.DashboardViewModel
import com.example.autoservice.ui.screens.details.RequestDetailsScreen
import com.example.autoservice.ui.screens.details.RequestDetailsViewModel
import com.example.autoservice.ui.screens.history.HistoryScreen
import com.example.autoservice.ui.screens.history.HistoryViewModel
import com.example.autoservice.ui.screens.reminders.RemindersScreen
import com.example.autoservice.ui.screens.reminders.RemindersViewModel

private val mainRoutes = setOf(
    Screen.Dashboard.route,
    Screen.History.route,
    Screen.Reminders.route,
    Screen.Analytics.route
)

@Composable
fun NavGraph() {
    val context = LocalContext.current
    val database = remember(context) { AppDatabase.getInstance(context) }

    val autoRepository = remember(database) { AutoRepository(database.autoDao()) }
    val requestRepository = remember(database) { RequestRepository(database.requestDao()) }
    val itemRepository = remember(database) { RequestItemRepository(database.requestItemDao()) }
    val reminderRepository = remember(database) { ReminderRepository(database.reminderDao()) }
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in mainRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = backStackEntry?.destination?.hierarchy?.any {
                            it.route == Screen.Dashboard.route
                        } == true,
                        onClick = { navController.navigateTopLevel(Screen.Dashboard.route) },
                        icon = { Icon(Icons.Default.Build, contentDescription = null) },
                        label = { Text(stringResource(R.string.navigation_requests)) }
                    )
                    NavigationBarItem(
                        selected = backStackEntry?.destination?.hierarchy?.any {
                            it.route == Screen.History.route
                        } == true,
                        onClick = { navController.navigateTopLevel(Screen.History.route) },
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text(stringResource(R.string.navigation_history)) }
                    )
                    NavigationBarItem(
                        selected = backStackEntry?.destination?.hierarchy?.any {
                            it.route == Screen.Reminders.route
                        } == true,
                        onClick = { navController.navigateTopLevel(Screen.Reminders.route) },
                        icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                        label = { Text(stringResource(R.string.navigation_reminders)) }
                    )
                    NavigationBarItem(
                        selected = backStackEntry?.destination?.hierarchy?.any {
                            it.route == Screen.Analytics.route
                        } == true,
                        onClick = { navController.navigateTopLevel(Screen.Analytics.route) },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                        label = { Text(stringResource(R.string.navigation_analytics)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(navController = navController)
            }

            composable(Screen.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModel.Factory(
                        autoRepository = autoRepository,
                        requestRepository = requestRepository
                    )
                )
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onRequestClick = { requestId ->
                        navController.navigate(Screen.RequestDetails.createRoute(requestId))
                    }
                )
            }

            composable(
                route = Screen.RequestDetails.route,
                arguments = listOf(navArgument("requestId") { type = NavType.LongType })
            ) { entry ->
                val requestId = entry.arguments?.getLong("requestId") ?: return@composable
                val detailsViewModel: RequestDetailsViewModel = viewModel(
                    key = "request-details-$requestId",
                    factory = RequestDetailsViewModel.Factory(
                        requestId = requestId,
                        autoRepository = autoRepository,
                        requestRepository = requestRepository,
                        itemRepository = itemRepository
                    )
                )
                RequestDetailsScreen(
                    viewModel = detailsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                val historyViewModel: HistoryViewModel = viewModel(
                    factory = HistoryViewModel.Factory(requestRepository)
                )
                HistoryScreen(
                    viewModel = historyViewModel,
                    onRequestClick = { requestId ->
                        navController.navigate(Screen.RequestDetails.createRoute(requestId))
                    }
                )
            }

            composable(Screen.Reminders.route) {
                val remindersViewModel: RemindersViewModel = viewModel(
                    factory = RemindersViewModel.Factory(
                        autoRepository = autoRepository,
                        reminderRepository = reminderRepository
                    )
                )
                RemindersScreen(viewModel = remindersViewModel)
            }

            composable(Screen.Analytics.route) {
                val analyticsViewModel: AnalyticsViewModel = viewModel(
                    factory = AnalyticsViewModel.Factory(
                        requestRepository = requestRepository,
                        itemRepository = itemRepository
                    )
                )
                AnalyticsScreen(viewModel = analyticsViewModel)
            }
        }
    }
}

private fun androidx.navigation.NavController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(Screen.Dashboard.route) {
            inclusive = false
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}