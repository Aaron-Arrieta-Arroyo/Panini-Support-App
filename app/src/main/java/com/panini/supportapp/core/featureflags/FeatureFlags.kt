package com.panini.supportapp.core.featureflags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Centralized Feature Flag registry.
 *
 * Flags use Compose [mutableStateOf] so any composable that reads them
 * recomposes automatically when a value changes — no manual refresh needed.
 *
 * All flags live here: one file, one place, zero ambiguity for future engineers.
 *
 * To evolve: replace the local mutableStateOf values with a remote source
 * (Firebase Remote Config, LaunchDarkly, etc.) inside AppContainer at startup.
 * Call sites never change.
 *
 * Active flags:
 *   - ticketCreationEnabled  : shows/hides the Create Ticket FAB and screen
 *   - priorityUpdateEnabled  : shows/hides "Change Priority" button in detail
 */
object FeatureFlags {
    var ticketCreationEnabled by mutableStateOf(true)
    var priorityUpdateEnabled by mutableStateOf(true)
}
