package com.panini.supportapp.domain.model

enum class Priority(val label: String, val sortOrder: Int) {
    HIGH("High", 0),
    MEDIUM("Medium", 1),
    LOW("Low", 2)
}
