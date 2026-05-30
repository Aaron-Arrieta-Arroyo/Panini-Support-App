package com.panini.supportapp.core.events

import com.panini.supportapp.domain.model.Priority
import com.panini.supportapp.domain.model.Ticket
import com.panini.supportapp.domain.model.TicketStatus

/**
 * Sealed class representing all events that can propagate through the system.
 * New events should be added here to keep the contract centralized.
 */
sealed class TicketEvent {
    data class TicketCreated(val ticket: Ticket) : TicketEvent()
    data class PriorityUpdated(val ticketId: String, val newPriority: Priority) : TicketEvent()
    data class StatusUpdated(val ticketId: String, val newStatus: TicketStatus) : TicketEvent()
}
