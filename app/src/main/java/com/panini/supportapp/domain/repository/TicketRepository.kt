package com.panini.supportapp.domain.repository

import com.panini.supportapp.domain.model.Priority
import com.panini.supportapp.domain.model.Ticket
import com.panini.supportapp.domain.model.TicketStatus

/**
 * Contract for ticket data operations.
 *
 * The presentation layer depends only on this interface, never on concrete
 * implementations. This allows swapping MockTicketRepository → RemoteTicketRepository
 * in AppContainer without touching any ViewModel or Composable.
 */
interface TicketRepository {
    suspend fun getTickets(): List<Ticket>
    suspend fun getTicketById(id: String): Ticket?
    suspend fun createTicket(ticket: Ticket): Ticket
    suspend fun updateTicketStatus(id: String, status: TicketStatus): Ticket
    suspend fun updateTicketPriority(id: String, priority: Priority): Ticket
}
