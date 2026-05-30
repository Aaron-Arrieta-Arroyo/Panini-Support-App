package com.panini.supportapp.presentation.ticketdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panini.supportapp.core.events.TicketEvent
import com.panini.supportapp.core.events.TicketEventBus
import com.panini.supportapp.domain.model.Priority
import com.panini.supportapp.domain.model.Ticket
import com.panini.supportapp.domain.model.TicketStatus
import com.panini.supportapp.domain.repository.TicketRepository
import com.panini.supportapp.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketDetailViewModel(
    private val ticketId: String,
    private val repository: TicketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Ticket>>(UiState.Loading)
    val uiState: StateFlow<UiState<Ticket>> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init {
        loadTicket()
    }

    fun loadTicket() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { repository.getTicketById(ticketId) }
                .onSuccess { ticket ->
                    _uiState.value = if (ticket != null) UiState.Success(ticket)
                    else UiState.Error("Ticket no encontrado")
                }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun updateStatus(status: TicketStatus) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            runCatching { repository.updateTicketStatus(ticketId, status) }
                .onSuccess { updated ->
                    _uiState.value = UiState.Success(updated)
                    _actionState.value = ActionState.Idle
                    // Emit event so TicketListViewModel reacts without a manual refresh
                    TicketEventBus.emit(TicketEvent.StatusUpdated(ticketId, status))
                }
                .onFailure {
                    _actionState.value = ActionState.Error(it.message ?: "Error al actualizar estado")
                }
        }
    }

    fun updatePriority(priority: Priority) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            runCatching { repository.updateTicketPriority(ticketId, priority) }
                .onSuccess { updated ->
                    _uiState.value = UiState.Success(updated)
                    _actionState.value = ActionState.Idle
                    // Emit event: list will re-sort automatically
                    TicketEventBus.emit(TicketEvent.PriorityUpdated(ticketId, priority))
                }
                .onFailure {
                    _actionState.value = ActionState.Error(it.message ?: "Error al actualizar prioridad")
                }
        }
    }

    sealed class ActionState {
        data object Idle : ActionState()
        data object Loading : ActionState()
        data class Error(val message: String) : ActionState()
    }

    companion object {
        fun factory(ticketId: String, repository: TicketRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    TicketDetailViewModel(ticketId, repository) as T
            }
    }
}
