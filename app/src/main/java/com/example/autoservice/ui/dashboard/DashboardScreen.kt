package com.example.autoservice.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.autoservice.ui.components.AppCard
import com.example.autoservice.ui.components.StatusChip
import com.example.autoservice.util.DateUtils

private data class StatusFilter(val code: String?, val labelRes: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onRequestClick: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.requests_title))
                        Text(
                            text = DateUtils.formatDate(System.currentTimeMillis()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateSheet = true },
                icon = { androidx.compose.material3.Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add_request)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SummaryCard(
                inProgress = state.inProgressCount,
                ready = state.readyCount,
                closedToday = state.closedTodayCount
            )

            val filters = listOf(
                StatusFilter(null, R.string.filter_all),
                StatusFilter("ACCEPTED", R.string.status_accepted),
                StatusFilter("DIAGNOSTICS", R.string.status_diagnostics),
                StatusFilter("APPROVAL", R.string.status_approval),
                StatusFilter("REPAIR", R.string.status_repair),
                StatusFilter("READY", R.string.status_ready),
                StatusFilter("CLOSED", R.string.status_closed)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = state.selectedStatus == filter.code,
                        onClick = { viewModel.selectStatus(filter.code) },
                        label = { Text(stringResource(filter.labelRes)) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.requests, key = { it.id }) { request ->
                    RequestCard(request = request, onClick = { onRequestClick(request.id) })
                }
            }
        }
    }

    if (showCreateSheet) {
        CreateRequestSheet(
            onDismiss = { showCreateSheet = false },
            onCreate = { plate, brand, vin, mileage, owner, phone ->
                viewModel.createRequest(
                    plateNumber = plate,
                    brandModel = brand,
                    vin = vin,
                    mileage = mileage,
                    ownerName = owner,
                    ownerPhone = phone,
                    onComplete = { showCreateSheet = false }
                )
            }
        )
    }
}

@Composable
private fun SummaryCard(
    inProgress: Int,
    ready: Int,
    closedToday: Int
) {
    AppCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryValue(R.string.summary_in_progress, inProgress)
            SummaryValue(R.string.summary_ready, ready)
            SummaryValue(R.string.summary_closed_today, closedToday)
        }
    }
}

@Composable
private fun SummaryValue(labelRes: Int, value: Int) {
    Column(modifier = Modifier.width(94.dp)) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun RequestCard(
    request: Request,
    onClick: () -> Unit
) {
    AppCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = request.plateNumber,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                StatusChip(request.status)
            }
            Text(
                text = request.brandModel,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = request.ownerName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = DateUtils.formatDateTime(request.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}