package com.panini.supportapp.data.dto

import com.google.gson.annotations.SerializedName

/** Raw API response model for a ticket. Mapped to domain Ticket via toDomain(). */
data class TicketDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("status") val status: String,
    @SerializedName("supplier") val supplier: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("category") val category: String,
    @SerializedName("assigned_to") val assignedTo: String?,
    @SerializedName("affected_quantity") val affectedQuantity: Int?
)

data class TicketListResponseDto(
    @SerializedName("data") val data: List<TicketDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int
)
