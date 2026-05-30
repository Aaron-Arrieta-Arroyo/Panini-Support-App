# Technical Decisions — Panini Support App

Handoff document for engineers continuing the project.  
Explains **what** was built, **why** each tool was chosen, and **how** to extend it.

---

## 1. Architectural pattern: MVVM + Repository

### Why MVVM

MVVM separates UI (Composables) from state (ViewModel) and data (Repository).  
The concrete benefit for this project:

- The ViewModel does not import any Android UI types (`Context`, `Activity`, etc.), so it can be tested without an emulator.
- The Composable only observes a `StateFlow` — it does not decide or process, it only renders.
- The Repository is an interface: switching from mock to a real backend does not touch the ViewModel or the screen.

### Why not Clean Architecture with UseCases

A short-term PoC with a small team **does not justify** the overhead of UseCases. Adding them here would be complexity without real benefit today.

If the project grows: inserting UseCases between ViewModel and Repository is a local change that does not require touching screens or repositories.

### Layer structure

```
presentation/   Composables + ViewModels (observe StateFlow)
domain/         Business models + TicketRepository interface
data/           DTOs, Retrofit, MockTicketRepository
core/           Cross-cutting tools (events, feature flags)
```

---

## 2. Event-based communication: SharedFlow

### Problem it solves

When the user creates a ticket or changes its priority, the list screen must update **without the user having to go back and reload**.

The obvious solution (calling `loadTickets()` after every action) would work, but it would re-run the full repository call — in production that is an unnecessary network request.

### Solution: `TicketEventBus` with `SharedFlow`

```kotlin
object TicketEventBus {
    private val _events = MutableSharedFlow<TicketEvent>()
    val events: SharedFlow<TicketEvent> = _events.asSharedFlow()
    suspend fun emit(event: TicketEvent) = _events.emit(event)
}
```

It is an application-level event bus. Any ViewModel can emit or listen.

**Flow for ticket creation:**

```
CreateTicketViewModel
  → repository.createTicket(ticket)         // persists the ticket
  → TicketEventBus.emit(TicketCreated)      // notifies the bus

TicketListViewModel (active collector in viewModelScope)
  → receives TicketCreated
  → inserts the ticket into the local list
  → re-sorts by priority
  → emits new state to StateFlow
  → Composable recomposes automatically
```

**Flow for priority update:**

```
TicketDetailViewModel
  → repository.updateTicketPriority(id, priority)
  → TicketEventBus.emit(PriorityUpdated)

TicketListViewModel
  → updates the ticket in the list
  → re-sorts: HIGH (sortOrder=0) moves up, LOW (sortOrder=2) moves down
  → Composable recomposes — the ticket changes position without reloading
```

### Why SharedFlow and not other options

| Alternative | Reason not to use it |
|---|---|
| Call `loadTickets()` again | Unnecessary re-fetch, not truly reactive |
| `LiveData` | Requires `LifecycleOwner`, not Kotlin-native |
| Callback between ViewModels | Tight coupling, hard to maintain |
| `SharedFlow` ✓ | Lifecycle-independent, supports multiple collectors, native Kotlin coroutines |

`replay = 0` (default): a new collector does not receive past events. This prevents a newly opened screen from reacting to events that already occurred.

---

## 3. Feature Flags

### Why Feature Flags

The business needs to **enable or disable features quickly** during internal testing without changing code or shipping a new release.

### Implementation

```kotlin
object FeatureFlags {
    var ticketCreationEnabled by mutableStateOf(true)
    var priorityUpdateEnabled by mutableStateOf(true)
}
```

They use Compose `mutableStateOf`: when the value changes (from `SettingsScreen`), **every Composable that reads it recomposes automatically**. No extra ViewModel is needed.

**Flag 1 — `ticketCreationEnabled`:**  
Controls whether the "+" FAB appears on the list. When disabled, the button disappears and the create screen is inaccessible.

**Flag 2 — `priorityUpdateEnabled`:**  
Controls whether the "Change Priority" button appears on the ticket detail screen. When disabled, status can still be changed but not priority.

### How to use it

```kotlin
// In any Composable:
if (FeatureFlags.ticketCreationEnabled) {
    FloatingActionButton(onClick = onCreateTicket) { ... }
}
```

The toggle in `SettingsScreen` modifies the flag directly — recomposition is immediate.

### How to evolve

To connect flags to a remote service (Firebase Remote Config, etc.):

```kotlin
// In AppContainer, at app startup:
val remoteFlags = RemoteConfigService.fetch()
FeatureFlags.ticketCreationEnabled = remoteFlags.ticketCreation
FeatureFlags.priorityUpdateEnabled = remoteFlags.priorityUpdate
```

No Composable or ViewModel changes.

---

## 4. Networking ready for the backend

The backend does not exist yet, but the structure is ready to connect without reorganizing the project.

**Current network layer:**

```
TicketApiService       — Retrofit interface with the 5 system endpoints
NetworkClient          — OkHttpClient + Retrofit configured, BASE_URL defined
RemoteTicketRepository — TicketRepository implementation using the API
MockTicketRepository   — current in-memory implementation
```

**To activate the real backend:** change **one line** in `AppContainer.kt`:

```kotlin
// Current (mock):
val ticketRepository: TicketRepository = MockTicketRepository()

// Production:
val ticketRepository: TicketRepository = RemoteTicketRepository(NetworkClient.ticketApiService)
```

No ViewModel or Composable knows which implementation is active.

---

## 5. Mock data

`MockData.kt` contains 10 tickets representing real Panini operational scenarios:

- Inventory shortages at points of sale
- Orders not delivered by suppliers
- Batch count errors
- Packaging damage during transport
- Invalid QR codes on packs
- Duplicate orders in the system

The data uses realistic fictional distributor company names and categories aligned with the context (`INVENTORY`, `DISTRIBUTION`, `LOGISTICS`, `SUPPLIER`, `QUALITY`).

---

## 6. Screen state handling

All screens that load data asynchronously use the same pattern:

```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

In the Composable:

```kotlin
when (val state = uiState) {
    is UiState.Loading -> CircularProgressIndicator()
    is UiState.Error   -> Text(state.message)
    is UiState.Success -> ContentComposable(state.data)
}
```

Guaranteed consistency: any new engineer recognizes the pattern on any screen.

---

## 7. Continuing the project

**If you need to add a new screen:**
1. Add `data object NewScreen : Screen("route")` in `Screen.kt`
2. Add `composable(Screen.NewScreen.route)` in `AppNavigation.kt`
3. Create `NewScreenScreen.kt` and `NewScreenViewModel.kt` in a new package under `presentation/`
4. Add the method to `TicketRepository` if new data is needed

**If you need to add an endpoint:**
1. Add the method in `TicketApiService.kt`
2. Add the corresponding DTO in `data/dto/`
3. Implement in `RemoteTicketRepository` and `MockTicketRepository`
4. Update `contracts/tickets-api.yaml`

**If you need to add a Feature Flag:**
1. Add `var newFlag by mutableStateOf(true)` in `FeatureFlags.kt`
2. Read the flag in the corresponding Composable
3. Add the toggle in `SettingsScreen.kt`

**Minimum stack to work on the project:**
- Kotlin, Jetpack Compose, Coroutines, StateFlow/SharedFlow
- MVVM pattern (ViewModel + Repository)
- Retrofit + Gson for networking
