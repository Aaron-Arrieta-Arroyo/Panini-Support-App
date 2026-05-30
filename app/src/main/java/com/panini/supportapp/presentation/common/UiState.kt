package com.panini.supportapp.presentation.common

/**
 * Generic wrapper for UI states. Every screen that loads async data uses this.
 * Keeps Loading / Success / Error handling consistent across the app.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
