package com.example.autoservice.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object RequestDetails : Screen("request/{requestId}") {
        fun createRoute(requestId: Long): String = "request/$requestId"
    }

    data object History : Screen("history")
    data object Reminders : Screen("reminders")
    data object Analytics : Screen("analytics")
}