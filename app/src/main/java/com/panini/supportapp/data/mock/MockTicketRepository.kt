package com.panini.supportapp.data.mock

import com.panini.supportapp.domain.model.Priority
import com.panini.supportapp.domain.model.Ticket
import com.panini.supportapp.domain.model.TicketStatus
import com.panini.supportapp.domain.repository.TicketRepository
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * In-memory implementation of TicketRepository using MockData.
 *
 * - Simulates network latency with [delay] so loading states are visible.
 * - Mutates a local list copy so state persists during the session.
 * - Returned lists are always sorted by priority (HIGH → MEDIUM → LOW).
 *
 * Replace with RemoteTicketRepository in AppContainer when the backend is ready.
 */
class MockTicketRepository : TicketRepository {

    private val tickets = MockData.tickets.toMutableList()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

    override suspend fun getTickets(): List<Ticket> {
        delay(500)
        return tickets.sortedBy { it.priority.sortOrder }
    }

    override suspend fun getTicketById(id: String): Ticket? {
        delay(300)
        return tickets.find { it.id == id }
    }

    override suspend fun createTicket(ticket: Ticket): Ticket {
        delay(600)
        val newTicket = ticket.copy(
            id = "TKT-${String.format("%03d", tickets.size + 1)}",
            status = TicketStatus.OPEN,
            createdAt = dateFormat.format(Date())
        )
        tickets.add(newTicket)
        return newTicket
    }

    override suspend fun updateTicketStatus(id: String, status: TicketStatus): Ticket {
        delay(400)
        val index = tickets.indexOfFirst { it.id == id }
        check(index != -1) { "Ticket $id not found" }
        val updated = tickets[index].copy(status = status)
        tickets[index] = updated
        return updated
    }

    override suspend fun updateTicketPriority(id: String, priority: Priority): Ticket {
        delay(400)
        val index = tickets.indexOfFirst { it.id == id }
        check(index != -1) { "Ticket $id not found" }
        val updated = tickets[index].copy(priority = priority)
        tickets[index] = updated
        return updated
    }
}
