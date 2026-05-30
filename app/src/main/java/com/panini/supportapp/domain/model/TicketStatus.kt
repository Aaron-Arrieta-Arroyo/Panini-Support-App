package com.panini.supportapp.domain.model

enum class TicketStatus(val label: String) {
    OPEN("Abierto"),
    IN_PROGRESS("En Progreso"),
    RESOLVED("Resuelto"),
    CLOSED("Cerrado")
}
