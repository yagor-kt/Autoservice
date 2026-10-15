package com.example.autoservice.ui.screens.reminders

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.autoservice.R
import com.example.autoservice.data.Auto
import com.example.autoservice.data.Reminder
import com.example.autoservice.ui.components.AppCard
import com.example.autoservice.ui.components.PrimaryButton
import com.example.autoservice.util.DateUtils
import com.example.autoservice.util.reminderColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(viewModel: RemindersViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateSheet by remember { mutableStateOf(false) }
    var reminderToDelete by remember { mutableStateOf<Reminder?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.reminders_title)) })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateSheet = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add_reminder)) }
            )
        }
    ) { innerPadding ->
        if (state.reminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.size(12.dp))
                    Text(stringResource(R.string.no_reminders))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.reminders, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onLongClick = { reminderToDelete = reminder }
                    )
                }
            }
        }
    }

    if (showCreateSheet) {
        CreateReminderSheet(
            autos = state.autos,
            onDismiss = { showCreateSheet = false },
            onSave = { auto, date ->
                viewModel.addReminder(auto, date) { saved ->
                    if (saved) showCreateSheet = false
                }
            }
        )
    }

    reminderToDelete?.let { reminder ->
        AlertDialog(
            onDismissRequest = { reminderToDelete = null },
            title = { Text(stringResource(R.string.delete_reminder_title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteReminder(reminder)
                        reminderToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { reminderToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReminderCard(
    reminder: Reminder,
    onLongClick: () -> Unit
) {
    val days = DateUtils.daysUntil(reminder.dueDate)
    val labelRes = when {
        days > 30 -> R.string.reminder_planned
        days in 8..30 -> R.string.reminder_soon
        else -> R.string.reminder_urgent
    }

    AppCard(
        modifier = Modifier.combinedClickable(
            onClick = {},
            onLongClick = onLongClick
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = reminder.plateNumber,
                style = MaterialTheme.typography.titleLarge
            )
            Text(reminder.brandModel)
            Text(
                text = stringResource(
                    R.string.next_maintenance,
                    DateUtils.formatDate(reminder.dueDate)
                )
            )
            Text(
                text = stringResource(R.string.days_remaining, days),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(labelRes),
                color = reminderColor(days),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateReminderSheet(
    autos: List<Auto>,
    onDismiss: () -> Unit,
    onSave: (Auto, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedAuto by remember { mutableStateOf<Auto?>(null) }
    var dateText by remember { mutableStateOf("") }
    var showDateError by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.add_reminder),
                style = MaterialTheme.typography.titleLarge
            )

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        selectedAuto?.let { "${it.plateNumber} — ${it.brandModel}" }
                            ?: stringResource(R.string.select_auto)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    autos.forEach { auto ->
                        DropdownMenuItem(
                            text = { Text("${auto.plateNumber} — ${auto.brandModel}") },
                            onClick = {
                                selectedAuto = auto
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = dateText,
                onValueChange = {
                    dateText = it
                    showDateError = false
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.maintenance_date)) },
                placeholder = { Text(stringResource(R.string.date_format_hint)) },
                singleLine = true,
                isError = showDateError
            )

            if (showDateError) {
                Text(
                    text = stringResource(R.string.invalid_date),
                    color = MaterialTheme.colorScheme.error
                )
            }

            PrimaryButton(
                text = stringResource(R.string.save),
                onClick = {
                    val auto = selectedAuto
                    if (auto == null || DateUtils.parseDate(dateText) == null) {
                        showDateError = true
                    } else {
                        onSave(auto, dateText)
                    }
                },
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}