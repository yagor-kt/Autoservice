package com.example.autoservice.ui.screens.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.autoservice.R
import com.example.autoservice.data.CountByBrand
import com.example.autoservice.data.CountByTitle
import com.example.autoservice.ui.components.AppCard
import com.example.autoservice.util.MoneyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.analytics_title)) })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                MetricCard(
                    title = stringResource(R.string.requests_this_month),
                    value = state.closedThisMonth.toString()
                )
            }
            item {
                MetricCard(
                    title = stringResource(R.string.revenue_this_month),
                    value = MoneyUtils.format(state.revenueThisMonth)
                )
            }
            item {
                MetricCard(
                    title = stringResource(R.string.revenue_this_week),
                    value = MoneyUtils.format(state.revenueThisWeek)
                )
            }
            item {
                MetricCard(
                    title = stringResource(R.string.average_check),
                    value = MoneyUtils.format(state.averageCheck)
                )
            }
            item {
                TitleRankingCard(
                    title = stringResource(R.string.top_works),
                    emptyText = stringResource(R.string.no_analytics_data),
                    rows = state.topWorks
                )
            }
            item {
                BrandRankingCard(
                    title = stringResource(R.string.most_frequent_brands),
                    emptyText = stringResource(R.string.no_analytics_data),
                    rows = state.topBrands
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String
) {
    AppCard {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TitleRankingCard(
    title: String,
    emptyText: String,
    rows: List<CountByTitle>
) {
    AppCard {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (rows.isEmpty()) {
                Text(emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                rows.forEach { row ->
                    Text(
                        text = stringResource(R.string.ranking_line, row.title, row.count),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun BrandRankingCard(
    title: String,
    emptyText: String,
    rows: List<CountByBrand>
) {
    AppCard {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (rows.isEmpty()) {
                Text(emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                rows.forEach { row ->
                    Text(
                        text = stringResource(R.string.ranking_line, row.brandModel, row.count),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}