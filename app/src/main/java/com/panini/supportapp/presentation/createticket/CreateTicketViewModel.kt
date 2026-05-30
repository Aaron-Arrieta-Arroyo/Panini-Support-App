package com.panini.supportapp.presentation.createticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panini.supportapp.core.events.TicketEvent
import com.panini.supportapp.core.events.TicketEventBus
import com.panini.supportapp.domain.model.Priority
import com.panini.supportapp.domain.model.Ticket
import com.panini.supportapp.domain.model.TicketCategory
import com.panini.supportapp.domain.model.TicketStatus
import com.panini.supportapp.domain.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CreateTicketUiState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val supplier: String = "",
    val category: TicketCategory = TicketCategory.INVENTORY,
    val affectedQuantity: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class CreateTicketViewModel(private val repository: TicketRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTicketUiState())
    val uiState: StateFlow<CreateTicketUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

    fun onTitleChange(v: String) = update { copy(title = v, error = null) }
    fun onDescriptionChange(v: String) = update { copy(description = v, error = null) }
    fun onPriorityChange(v: Priority) = update { copy(priority = v) }
    fun onSupplierChange(v: String) = update { copy(supplier = v, error = null) }
    fun onCategoryChange(v: TicketCategory) = update { copy(category = v) }
    fun onAffectedQuantityChange(v: String) = update { copy(affectedQuantity = v) }

    fun createTicket(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank() || state.description.isBlank() || state.supplier.isBlank()) {
            update { copy(error = "Título, descripción y proveedor son obligatorios") }
            return
        }

        viewModelScope.launch {
            update { copy(isLoading = true, error = null) }

            val ticket = Ticket(
                id = "",
                title = state.title.trim(),
                description = state.description.trim(),
                priority = state.priority,
                status = TicketStatus.OPEN,
                supplier = state.supplier.trim(),
                createdAt = dateFormat.format(Date()),
                category = state.category,
                affectedQuantity = state.affectedQuantity.toIntOrNull()
            )

            runCatching { repository.createTicket(ticket) }
                .onSuccess { created ->
                    // Broadcast to all subscribers (e.g. TicketListViewModel)
                    TicketEventBus.emit(TicketEvent.TicketCreated(created))
                    update { copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure {
                    update { copy(isLoading = false, error = it.message ?: "Error al crear ticket") }
                }
        }
    }

    private fun update(block: CreateTicketUiState.() -> CreateTicketUiState) {
        _uiState.value = _uiState.value.block()
    }

    companion object {
        fun factory(repository: TicketRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                CreateTicketViewModel(repository) as T
        }
    }
}
