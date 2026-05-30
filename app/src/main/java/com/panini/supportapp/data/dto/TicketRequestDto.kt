package com.panini.supportapp.data.dto

import com.google.gson.annotations.SerializedName

data class CreateTicketRequestDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("supplier") val supplier: String,
    @SerializedName("category") val category: String,
    @SerializedName("affected_quantity") val affectedQuantity: Int?
)

data class UpdateStatusRequestDto(
    @SerializedName("status") val status: String
)

data class UpdatePriorityRequestDto(
    @SerializedName("priority") val priority: String
)
