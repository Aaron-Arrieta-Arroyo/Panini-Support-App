package com.panini.supportapp.data.remote

import com.panini.supportapp.data.dto.CreateTicketRequestDto
import com.panini.supportapp.data.dto.TicketDto
import com.panini.supportapp.data.dto.UpdatePriorityRequestDto
import com.panini.supportapp.data.dto.UpdateStatusRequestDto
import com.panini.supportapp.domain.model.Priority
import com.panini.supportapp.domain.model.Ticket
import com.panini.supportapp.domain.model.TicketCategory
import com.panini.supportapp.domain.model.TicketStatus
import com.panini.supportapp.domain.repository.TicketRepository

/**
 * Production implementation of TicketRepository.
 * Delegates all calls to the Retrofit service and maps DTOs to domain models.
 *
 * To activate: replace MockTicketRepository with RemoteTicketRepository
 * in AppContainer. Zero ViewModel changes required.
 */
class RemoteTicketRepository(
    private val apiService: TicketApiService
) : TicketRepository {

    override suspend fun getTickets(): List<Ticket> =
        apiService.getTickets().data.map { it.toDomain() }

    override suspend fun getTicketById(id: String): Ticket =
        apiService.getTicketById(id).toDomain()

    override suspend fun createTicket(ticket: Ticket): Ticket {
        val request = CreateTicketRequestDto(
            title = ticket.title,
            description = ticket.description,
            priority = ticket.priority.name,
            supplier = ticket.supplier,
            category = ticket.category.name,
            affectedQuantity = ticket.affectedQuantity
        )
        return apiService.createTicket(request).toDomain()
    }

    override suspend fun updateTicketStatus(id: String, status: TicketStatus): Ticket =
        apiService.updateTicketStatus(id, UpdateStatusRequestDto(status.name)).toDomain()

    override suspend fun updateTicketPriority(id: String, priority: Priority): Ticket =
        apiService.updateTicketPriority(id, UpdatePriorityRequestDto(priority.name)).toDomain()

    private fun TicketDto.toDomain(): Ticket = Ticket(
        id = id,
        title = title,
        description = description,
        priority = Priority.valueOf(priority),
        status = TicketStatus.valueOf(status),
        supplier = supplier,
        createdAt = createdAt,
        category = TicketCategory.valueOf(category),
        assignedTo = assignedTo,
        affectedQuantity = affectedQuantity
    )
}
