package com.panini.supportapp.domain.model

/**
 * Core domain model for a support ticket.
 * This is the single source of truth used across the presentation layer.
 * DTOs are mapped to/from this model in the data layer.
 */
data class Ticket(
    val id: String,
    val title: String,
    val description: String,
    val priority: Priority,
    val status: TicketStatus,
    val supplier: String,
    val createdAt: String,
    val category: TicketCategory,
    val assignedTo: String? = null,
    val affectedQuantity: Int? = null
)
