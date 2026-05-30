package com.panini.supportapp.presentation.ticketdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panini.supportapp.core.featureflags.FeatureFlags
import com.panini.supportapp.domain.model.Priority
import com.panini.supportapp.domain.model.Ticket
import com.panini.supportapp.domain.model.TicketStatus
import com.panini.supportapp.domain.repository.TicketRepository
import com.panini.supportapp.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: String,
    repository: TicketRepository,
    onBack: () -> Unit,
    viewModel: TicketDetailViewModel = viewModel(
        factory = TicketDetailViewModel.factory(ticketId, repository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Ticket") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UiState.Error -> Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                is UiState.Success -> TicketDetailContent(
                    ticket = state.data,
                    actionState = actionState,
                    onUpdateStatus = viewModel::updateStatus,
                    onUpdatePriority = viewModel::updatePriority
                )
            }
        }
    }
}

@Composable
private fun TicketDetailContent(
    ticket: Ticket,
    actionState: TicketDetailViewModel.ActionState,
    onUpdateStatus: (TicketStatus) -> Unit,
    onUpdatePriority: (Priority) -> Unit
) {
    var showStatusDialog by remember { mutableStateOf(false) }
    var showPriorityDialog by remember { mutableStateOf(false) }
    val isActionLoading = actionState is TicketDetailViewModel.ActionState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = ticket.id, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = ticket.title, style = MaterialTheme.typography.headlineSmall)

        HorizontalDivider()

        DetailRow("Estado", ticket.status.label)
        DetailRow("Prioridad", ticket.priority.label)
        DetailRow("Proveedor", ticket.supplier)
        DetailRow("Categoría", ticket.category.label)
        DetailRow("Fecha de creación", ticket.createdAt.take(10))
        ticket.assignedTo?.let { DetailRow("Asignado a", it) }
        ticket.affectedQuantity?.let { DetailRow("Unidades afectadas", it.toString()) }

        HorizontalDivider()

        Text("Descripción", style = MaterialTheme.typography.titleSmall)
        Text(ticket.description, style = MaterialTheme.typography.bodyMedium)

        HorizontalDivider()

        if (actionState is TicketDetailViewModel.ActionState.Error) {
            Text(text = actionState.message, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { showStatusDialog = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isActionLoading
        ) { Text("Cambiar Estado") }

        // Feature Flag: hide priority update button if flag is off
        if (FeatureFlags.priorityUpdateEnabled) {
            OutlinedButton(
                onClick = { showPriorityDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isActionLoading
            ) { Text("Cambiar Prioridad") }
        }
    }

    if (showStatusDialog) {
        OptionsDialog(
            title = "Seleccionar Estado",
            options = TicketStatus.entries.map { it.label },
            onSelect = { index ->
                onUpdateStatus(TicketStatus.entries[index])
                showStatusDialog = false
            },
            onDismiss = { showStatusDialog = false }
        )
    }

    if (showPriorityDialog) {
        OptionsDialog(
            title = "Seleccionar Prioridad",
            options = Priority.entries.map { it.label },
            onSelect = { index ->
                onUpdatePriority(Priority.entries[index])
                showPriorityDialog = false
            },
            onDismiss = { showPriorityDialog = false }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun OptionsDialog(
    title: String,
    options: List<String>,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEachIndexed { index, option ->
                    TextButton(
                        onClick = { onSelect(index) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(option) }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
