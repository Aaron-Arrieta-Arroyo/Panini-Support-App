package com.panini.supportapp.core.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Application-level event bus using SharedFlow.
 *
 * Chosen over LiveData because SharedFlow:
 * - is lifecycle-independent (no need to pass LifecycleOwner)
 * - supports multiple collectors (fan-out)
 * - does not deliver stale events to new subscribers (replay = 0)
 *
 * Usage:
 *   Emit  → TicketEventBus.emit(TicketEvent.TicketCreated(ticket))
 *   Collect → TicketEventBus.events.collect { event -> ... }
 */
object TicketEventBus {

    private val _events = MutableSharedFlow<TicketEvent>()
    val events: SharedFlow<TicketEvent> = _events.asSharedFlow()

    suspend fun emit(event: TicketEvent) {
        _events.emit(event)
    }
}
