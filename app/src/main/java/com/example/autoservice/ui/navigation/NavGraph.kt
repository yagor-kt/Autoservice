package com.example.autoservice.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.autoservice.data.AppDatabase
import com.example.autoservice.data.AutoRepository
import com.example.autoservice.data.ReminderRepository
import com.example.autoservice.data.RequestItemRepository
import com.example.autoservice.data.RequestRepository
import com.example.autoservice.ui.screens.auth.LoginScreen
import com.example.autoservice.ui.screens.dashboard.DashboardScreen
import com.example.autoservice.ui.screens.dashboard.DashboardViewModel
import com.example.autoservice.ui.screens.details.RequestDetailsScreen
import com.example.autoservice.ui.screens.details.RequestDetailsViewModel

@Composable
fun NavGraph() {
    val context = LocalContext.current
    val database = remember(context) { AppDatabase.getInstance(context) }

    val autoRepository = remember(database) { AutoRepository(database.autoDao()) }
    val requestRepository = remember(database) { RequestRepository(database.requestDao()) }
    val itemRepository = remember(database) { RequestItemRepository(database.requestItemDao()) }
    val reminderRepository = remember(database) { ReminderRepository(database.reminderDao()) }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
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
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getLong("requestId") ?: return@composable
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
    }
}

@Suppress("UNCHECKED_CAST")
internal class SimpleViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
}