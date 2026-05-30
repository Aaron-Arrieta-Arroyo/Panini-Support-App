package com.panini.supportapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object TicketList : Screen("ticket_list")
    data object CreateTicket : Screen("create_ticket")
    data object Settings : Screen("settings")
    data object TicketDetail : Screen("ticket_detail/{ticketId}") {
        fun createRoute(ticketId: String) = "ticket_detail/$ticketId"
    }
}
