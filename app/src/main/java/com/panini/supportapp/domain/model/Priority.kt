package com.panini.supportapp.domain.model

enum class Priority(val label: String, val sortOrder: Int) {
    HIGH("Alta", 0),
    MEDIUM("Media", 1),
    LOW("Baja", 2)
}
