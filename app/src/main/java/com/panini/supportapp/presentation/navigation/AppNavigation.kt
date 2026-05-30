package com.panini.supportapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.panini.supportapp.domain.repository.TicketRepository
import com.panini.supportapp.presentation.createticket.CreateTicketScreen
import com.panini.supportapp.presentation.login.LoginScreen
import com.panini.supportapp.presentation.settings.SettingsScreen
import com.panini.supportapp.presentation.ticketdetail.TicketDetailScreen
import com.panini.supportapp.presentation.ticketlist.TicketListScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: TicketRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.TicketList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.TicketList.route) {
            TicketListScreen(
                repository = repository,
                onTicketClick = { ticketId ->
                    navController.navigate(Screen.TicketDetail.createRoute(ticketId))
                },
                onCreateTicket = {
                    navController.navigate(Screen.CreateTicket.route)
                },
                onSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.TicketDetail.route,
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: return@composable
            TicketDetailScreen(
                ticketId = ticketId,
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateTicket.route) {
            CreateTicketScreen(
                repository = repository,
                onTicketCreated = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
