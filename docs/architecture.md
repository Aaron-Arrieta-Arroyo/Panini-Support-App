# Decisiones Técnicas — Panini Support App

Documento de handoff para ingenieros que continúen el proyecto.  
Explica **qué** se construyó, **por qué** se eligió cada herramienta y **cómo** extenderlo.

---

## 1. Patrón arquitectónico: MVVM + Repository

### Por qué MVVM

MVVM separa la UI (Composables) del estado (ViewModel) y de los datos (Repository).  
El beneficio concreto para este proyecto:

- El ViewModel no importa nada de Android UI (`Context`, `Activity`, etc.), por lo que puede ser testeado sin emulador.
- El Composable solo observa un `StateFlow` — no decide ni procesa, solo dibuja.
- El Repository es una interfaz: cambiar de mock a backend real no toca ni el ViewModel ni la pantalla.

### Por qué no Clean Architecture con UseCases

Un PoC de corto plazo con un equipo pequeño **no justifica** el overhead de UseCases. Agregarlos aquí sería complejidad sin beneficio real hoy.

Si el proyecto crece: insertar UseCases entre ViewModel y Repository es una operación local que no requiere tocar pantallas ni repositorios.

### Estructura de capas

```
presentation/   Composables + ViewModels (observan StateFlow)
domain/         Modelos de negocio + interfaz TicketRepository
data/           DTOs, Retrofit, MockTicketRepository
core/           Herramientas transversales (eventos, feature flags)
```

---

## 2. Comunicación basada en eventos: SharedFlow

### Problema que resuelve

Cuando el usuario crea un ticket o cambia su prioridad, la pantalla de lista debe actualizarse **sin que el usuario tenga que volver atrás y recargar**.

La solución obvia (llamar `loadTickets()` después de cada acción) funcionaría, pero re-haría la llamada al repositorio completa — en producción eso es una petición de red innecesaria.

### Solución: `TicketEventBus` con `SharedFlow`

```kotlin
object TicketEventBus {
    private val _events = MutableSharedFlow<TicketEvent>()
    val events: SharedFlow<TicketEvent> = _events.asSharedFlow()
    suspend fun emit(event: TicketEvent) = _events.emit(event)
}
```

Es un bus de eventos a nivel de aplicación. Cualquier ViewModel puede emitir o escuchar.

**Flujo para creación de ticket:**

```
CreateTicketViewModel
  → repository.createTicket(ticket)         // persiste el ticket
  → TicketEventBus.emit(TicketCreated)      // notifica al bus

TicketListViewModel (collector activo en viewModelScope)
  → recibe TicketCreated
  → inserta el ticket en la lista local
  → re-ordena por prioridad
  → emite nuevo estado al StateFlow
  → Composable recompone automáticamente
```

**Flujo para actualización de prioridad:**

```
TicketDetailViewModel
  → repository.updateTicketPriority(id, priority)
  → TicketEventBus.emit(PriorityUpdated)

TicketListViewModel
  → actualiza el ticket en la lista
  → re-ordena: HIGH (sortOrder=0) sube, LOW (sortOrder=2) baja
  → Composable recompone — el ticket cambia de posición sin recargar
```

### Por qué SharedFlow y no otras opciones

| Alternativa | Razón para no usarla |
|---|---|
| Llamar `loadTickets()` de nuevo | Re-petición de red innecesaria, no es "reactivo" |
| `LiveData` | Requiere `LifecycleOwner`, no es Kotlin-nativo |
| Callback entre ViewModels | Acoplamiento fuerte, difícil de mantener |
| `SharedFlow` ✓ | Lifecycle-independiente, soporta múltiples colectores, Kotlin coroutines nativo |

`replay = 0` (por defecto): un colector nuevo no recibe eventos anteriores. Esto evita que una pantalla recién abierta reaccione a eventos que ya ocurrieron.

---

## 3. Feature Flags

### Por qué Feature Flags

La empresa necesita poder **habilitar o deshabilitar funcionalidades rápidamente** durante pruebas internas sin modificar código ni hacer un nuevo release.

### Implementación

```kotlin
object FeatureFlags {
    var ticketCreationEnabled by mutableStateOf(true)
    var priorityUpdateEnabled by mutableStateOf(true)
}
```

Se usan `mutableStateOf` de Compose: cuando el valor cambia (desde `SettingsScreen`), **todos los Composables que lo leen se recomponen automáticamente**. No se necesita ningún ViewModel adicional para esto.

**Flag 1 — `ticketCreationEnabled`:**  
Controla si el FAB "+" aparece en la lista. Si está desactivado, el botón desaparece y la pantalla de creación es inaccesible.

**Flag 2 — `priorityUpdateEnabled`:**  
Controla si el botón "Cambiar Prioridad" aparece en el detalle del ticket. Si está desactivado, el estado se puede cambiar pero no la prioridad.

### Cómo usarlo

```kotlin
// En cualquier Composable:
if (FeatureFlags.ticketCreationEnabled) {
    FloatingActionButton(onClick = onCreateTicket) { ... }
}
```

El toggle en `SettingsScreen` modifica el flag directamente — la recomposición es inmediata.

### Cómo evolucionar

Para conectar los flags a un servicio remoto (Firebase Remote Config, etc.):

```kotlin
// En AppContainer, al iniciar la app:
val remoteFlags = RemoteConfigService.fetch()
FeatureFlags.ticketCreationEnabled = remoteFlags.ticketCreation
FeatureFlags.priorityUpdateEnabled = remoteFlags.priorityUpdate
```

Ningún Composable ni ViewModel cambia.

---

## 4. Networking preparado para el backend

El backend no existe aún, pero la estructura está lista para conectarse sin reorganizar el proyecto.

**Capa de red actual:**

```
TicketApiService     — interfaz Retrofit con los 5 endpoints del sistema
NetworkClient        — OkHttpClient + Retrofit configurados, BASE_URL definida
RemoteTicketRepository — implementación de TicketRepository usando el API
MockTicketRepository   — implementación actual con datos en memoria
```

**Para activar el backend real:** cambiar **una línea** en `AppContainer.kt`:

```kotlin
// Actual (mock):
val ticketRepository: TicketRepository = MockTicketRepository()

// Producción:
val ticketRepository: TicketRepository = RemoteTicketRepository(NetworkClient.ticketApiService)
```

Ningún ViewModel ni Composable sabe qué implementación está activa.

---

## 5. Datos mock

`MockData.kt` contiene 10 tickets que representan escenarios reales de operación Panini:

- Faltantes de inventario en puntos de venta
- Pedidos no entregados por proveedores
- Errores en conteo de lotes
- Daños en empaque durante transporte
- Códigos QR inválidos en sobres
- Pedidos duplicados en el sistema

Los datos usan nombres reales de empresas ficticias de distribución y categorías coherentes con el contexto (`INVENTORY`, `DISTRIBUTION`, `LOGISTICS`, `SUPPLIER`, `QUALITY`).

---

## 6. Manejo de estados de pantalla

Todas las pantallas que cargan datos async usan el mismo patrón:

```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

En el Composable:

```kotlin
when (val state = uiState) {
    is UiState.Loading -> CircularProgressIndicator()
    is UiState.Error   -> Text(state.message)
    is UiState.Success -> ContentComposable(state.data)
}
```

Consistencia garantizada: cualquier ingeniero nuevo reconoce el patrón en cualquier pantalla.

---

## 7. Para continuar el proyecto

**Si necesitás agregar una pantalla nueva:**
1. Agregar `data object NuevaPantalla : Screen("ruta")` en `Screen.kt`
2. Agregar `composable(Screen.NuevaPantalla.route)` en `AppNavigation.kt`
3. Crear `NuevaPantallaScreen.kt` y `NuevaPantallaViewModel.kt` en un nuevo package bajo `presentation/`
4. Agregar el método al `TicketRepository` si se necesitan datos nuevos

**Si necesitás agregar un endpoint:**
1. Agregar el método en `TicketApiService.kt`
2. Agregar el DTO correspondiente en `data/dto/`
3. Implementar en `RemoteTicketRepository` y `MockTicketRepository`
4. Actualizar `contracts/tickets-api.yaml`

**Si necesitás agregar un Feature Flag:**
1. Agregar `var nuevoFlag by mutableStateOf(true)` en `FeatureFlags.kt`
2. Leer el flag en el Composable correspondiente
3. Agregar el toggle en `SettingsScreen.kt`

**Stack mínimo para trabajar en el proyecto:**
- Kotlin, Jetpack Compose, Coroutines, StateFlow/SharedFlow
- Patrón MVVM (ViewModel + Repository)
- Retrofit + Gson para networking
