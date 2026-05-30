package com.panini.supportapp

import android.app.Application
import com.panini.supportapp.data.mock.MockTicketRepository
import com.panini.supportapp.domain.repository.TicketRepository

/**
 * Application class.
 * AppContainer holds all dependencies (manual DI, no framework).
 *
 * To switch to the real backend:
 *   1. Change `MockTicketRepository()` → `RemoteTicketRepository(NetworkClient.ticketApiService)`
 *   2. Nothing else changes.
 */
class SupportApp : Application() {
    val container: AppContainer by lazy { AppContainer() }
}

class AppContainer {
    val ticketRepository: TicketRepository = MockTicketRepository()
}
