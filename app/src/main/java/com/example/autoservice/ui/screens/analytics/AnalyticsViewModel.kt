package com.example.autoservice.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.autoservice.data.CountByBrand
import com.example.autoservice.data.CountByTitle
import com.example.autoservice.data.RequestItemRepository
import com.example.autoservice.data.RequestRepository
import com.example.autoservice.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AnalyticsUiState(
    val closedThisMonth: Int = 0,
    val revenueThisMonth: Int = 0,
    val revenueThisWeek: Int = 0,
    val topWorks: List<CountByTitle> = emptyList(),
    val topBrands: List<CountByBrand> = emptyList()
) {
    val averageCheck: Int
        get() = if (closedThisMonth == 0) 0 else revenueThisMonth / closedThisMonth
}

class AnalyticsViewModel(
    requestRepository: RequestRepository,
    itemRepository: RequestItemRepository
) : ViewModel() {
    private val now = DateUtils.endOfDay()

    val uiState = combine(
        requestRepository.countClosedBetween(DateUtils.monthStart(), now),
        requestRepository.revenueClosedBetween(DateUtils.monthStart(), now),
        requestRepository.revenueClosedBetween(DateUtils.weekAgo(), now),
        itemRepository.topWorks(),
        requestRepository.topClosedBrands()
    ) { count, monthRevenue, weekRevenue, works, brands ->
        AnalyticsUiState(
            closedThisMonth = count,
            revenueThisMonth = monthRevenue,
            revenueThisWeek = weekRevenue,
            topWorks = works,
            topBrands = brands
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AnalyticsUiState()
    )

    class Factory(
        private val requestRepository: RequestRepository,
        private val itemRepository: RequestItemRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AnalyticsViewModel(requestRepository, itemRepository) as T
    }
}