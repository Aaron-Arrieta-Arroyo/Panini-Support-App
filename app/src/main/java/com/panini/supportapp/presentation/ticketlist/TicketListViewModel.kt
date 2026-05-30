package com.panini.supportapp.presentation.ticketlist

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

/**
 * ViewModel for the ticket list screen.
 *
 * Event-based communication:
 *   On init, this VM subscribes to TicketEventBus.events via a SharedFlow collector
 *   running in viewModelScope. When another screen emits a TicketCreated or
 *   PriorityUpdated event, this VM reacts without any manual screen refresh.
 *
 * The list is always re-sorted by priority after any mutation so high-priority
 * tickets stay at the top automatically.
 */
class TicketListViewModel(private val repository: TicketRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Ticket>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Ticket>>> = _uiState.asStateFlow()

    init {
        loadTickets()
        observeEvents()
    }

    fun loadTickets() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { repository.getTickets() }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Unknown error") }
        }
    }

    // Subscribes to the event bus for the lifetime of this ViewModel.
    private fun observeEvents() {
        viewModelScope.launch {
            TicketEventBus.events.collect { event ->
                when (event) {
                    is TicketEvent.TicketCreated -> insertAndSort(event.ticket)
                    is TicketEvent.PriorityUpdated -> updatePriorityAndSort(event.ticketId, event.newPriority)
                    is TicketEvent.StatusUpdated -> updateStatus(event.ticketId, event.newStatus)
                }
            }
        }
    }

    private fun insertAndSort(ticket: Ticket) {
        val current = successData() ?: return
        _uiState.value = UiState.Success((current + ticket).sortedBy { it.priority.sortOrder })
    }

    private fun updatePriorityAndSort(ticketId: String, priority: Priority) {
        val current = successData() ?: return
        val updated = current
            .map { if (it.id == ticketId) it.copy(priority = priority) else it }
            .sortedBy { it.priority.sortOrder }
        _uiState.value = UiState.Success(updated)
    }

    private fun updateStatus(ticketId: String, status: TicketStatus) {
        val current = successData() ?: return
        _uiState.value = UiState.Success(
            current.map { if (it.id == ticketId) it.copy(status = status) else it }
        )
    }

    private fun successData(): List<Ticket>? =
        (_uiState.value as? UiState.Success)?.data

    companion object {
        fun factory(repository: TicketRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                TicketListViewModel(repository) as T
        }
    }
}
