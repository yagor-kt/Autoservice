package com.example.autoservice.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.autoservice.R
import com.example.autoservice.data.Request
import com.example.autoservice.data.RequestItem
import com.example.autoservice.data.catalog.CatalogItem
import com.example.autoservice.data.catalog.PartCatalog
import com.example.autoservice.data.catalog.WorkCatalog
import com.example.autoservice.ui.components.AppCard
import com.example.autoservice.ui.components.PrimaryButton
import com.example.autoservice.ui.components.StatusChip
import com.example.autoservice.util.DateUtils
import com.example.autoservice.util.MoneyUtils
import com.example.autoservice.util.StatusMapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsScreen(
    viewModel: RequestDetailsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddSheet by remember { mutableStateOf<String?>(null) }
    var showCloseDialog by remember { mutableStateOf(false) }
    var showReceiptDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.request_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        val request = state.request
        if (request == null) {
            Text(
                text = stringResource(R.string.request_not_found),
                modifier = Modifier.padding(innerPadding).padding(24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    VehicleCard(
                        request = request,
                        vin = state.auto?.vin.orEmpty(),
                        mileage = state.auto?.mileage
                    )
                }
                item {
                    OwnerCard(
                        request = request,
                        phone = state.auto?.ownerPhone.orEmpty()
                    )
                }
                item {
                    AppCard {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatusChip(request.status)
                            if (request.status != "CLOSED") {
                                PrimaryButton(
                                    text = stringResource(R.string.next_stage),
                                    onClick = { viewModel.moveToNextStage(request) }
                                )
                            }
                        }
                    }
                }
                item {
                    ItemSection(
                        title = stringResource(R.string.works_title),
                        items = state.items.filter { it.type == "WORK" },
                        onAdd = { showAddSheet = "WORK" }
                    )
                }
                item {
                    ItemSection(
                        title = stringResource(R.string.parts_title),
                        items = state.items.filter { it.type == "PART" },
                        onAdd = { showAddSheet = "PART" }
                    )
                }
                item {
                    AppCard {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.total_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = MoneyUtils.format(state.total),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                if (request.status != "CLOSED") {
                    item {
                        PrimaryButton(
                            text = stringResource(R.string.close_request),
                            onClick = { showCloseDialog = true }
                        )
                    }
                }
                item {
                    PrimaryButton(
                        text = stringResource(R.string.print_receipt),
                        onClick = { showReceiptDialog = true }
                    )
                }
            }
        }
    }

    showAddSheet?.let { type ->
        AddCatalogItemSheet(
            type = type,
            onDismiss = { showAddSheet = null },
            onAdd = { catalogItem ->
                viewModel.addItem(type, catalogItem.title, catalogItem.price)
                showAddSheet = null
            }
        )
    }

    if (showCloseDialog) {
        AlertDialog(
            onDismissRequest = { showCloseDialog = false },
            title = { Text(stringResource(R.string.close_request_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.request?.let(viewModel::closeRequest)
                        showCloseDialog = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showReceiptDialog) {
        AlertDialog(
            onDismissRequest = { showReceiptDialog = false },
            title = { Text(stringResource(R.string.print_receipt)) },
            text = { Text(stringResource(R.string.payment_accepted)) },
            confirmButton = {
                TextButton(onClick = { showReceiptDialog = false }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun VehicleCard(
    request: Request,
    vin: String,
    mileage: Int?
) {
    AppCard {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(stringResource(R.string.vehicle_section), style = MaterialTheme.typography.titleMedium)
            Text(request.plateNumber, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(request.brandModel)
            Text("${stringResource(R.string.vin)}: ${vin.ifBlank { stringResource(R.string.unknown_value) }}")
            Text(
                "${stringResource(R.string.mileage)}: ${
                    mileage?.let { "$it ${stringResource(R.string.kilometers_short)}" }
                        ?: stringResource(R.string.unknown_value)
                }"
            )
        }
    }
}

@Composable
private fun OwnerCard(
    request: Request,
    phone: String
) {
    AppCard {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(stringResource(R.string.owner_section), style = MaterialTheme.typography.titleMedium)
            Text(request.ownerName)
            Text("${stringResource(R.string.owner_phone)}: ${phone.ifBlank { stringResource(R.string.unknown_value) }}")
            Text(
                "${stringResource(R.string.created_label)}: ${DateUtils.formatDateTime(request.createdAt)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ItemSection(
    title: String,
    items: List<RequestItem>,
    onAdd: () -> Unit
) {
    AppCard {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onAdd) {
                    Text(stringResource(R.string.add_item))
                }
            }
            if (items.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_items),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.title, modifier = Modifier.weight(1f))
                        Spacer(Modifier.size(8.dp))
                        Text(MoneyUtils.format(item.price))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCatalogItemSheet(
    type: String,
    onDismiss: () -> Unit,
    onAdd: (CatalogItem) -> Unit
) {
    val catalog = if (type == "WORK") WorkCatalog.items else PartCatalog.items
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<CatalogItem?>(null) }

    androidx.compose.material3.ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (type == "WORK") {
                    stringResource(R.string.add_work)
                } else {
                    stringResource(R.string.add_part)
                },
                style = MaterialTheme.typography.titleLarge
            )

            androidx.compose.material3.OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selected?.title ?: stringResource(R.string.select_item))
            }

            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                catalog.forEach { item ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("${item.title} — ${MoneyUtils.format(item.price)}") },
                        onClick = {
                            selected = item
                            expanded = false
                        }
                    )
                }
            }

            PrimaryButton(
                text = stringResource(R.string.add_item),
                onClick = { selected?.let(onAdd) },
                enabled = selected != null,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}