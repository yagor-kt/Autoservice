package com.example.autoservice.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.autoservice.data.Auto
import com.example.autoservice.data.AutoRepository
import com.example.autoservice.data.Request
import com.example.autoservice.data.RequestRepository
import com.example.autoservice.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val requests: List<Request> = emptyList(),
    val selectedStatus: String? = null,
    val inProgressCount: Int = 0,
    val readyCount: Int = 0,
    val closedTodayCount: Int = 0
)

class DashboardViewModel(
    private val autoRepository: AutoRepository,
    private val requestRepository: RequestRepository
) : ViewModel() {
    private val selectedStatus = MutableStateFlow<String?>(null)

    val uiState = combine(
        requestRepository.getAll(),
        selectedStatus
    ) { requests, filter ->
        val todayStart = DateUtils.startOfDay()
        val todayEnd = DateUtils.endOfDay()

        DashboardUiState(
            requests = if (filter == null) requests else requests.filter { it.status == filter },
            selectedStatus = filter,
            inProgressCount = requests.count {
                it.status in setOf("ACCEPTED", "DIAGNOSTICS", "APPROVAL", "REPAIR")
            },
            readyCount = requests.count { it.status == "READY" },
            closedTodayCount = requests.count {
                it.status == "CLOSED" && it.closedAt != null &&
                        it.closedAt in todayStart..todayEnd
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )

    fun selectStatus(status: String?) {
        selectedStatus.value = status
    }

    fun createRequest(
        plateNumber: String,
        brandModel: String,
        vin: String,
        mileage: Int,
        ownerName: String,
        ownerPhone: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val autoId = autoRepository.insert(
                Auto(
                    plateNumber = plateNumber.trim(),
                    brandModel = brandModel.trim(),
                    vin = vin.trim(),
                    mileage = mileage,
                    ownerName = ownerName.trim(),
                    ownerPhone = ownerPhone.trim()
                )
            )

            requestRepository.insert(
                Request(
                    autoId = autoId,
                    plateNumber = plateNumber.trim(),
                    brandModel = brandModel.trim(),
                    ownerName = ownerName.trim(),
                    status = "ACCEPTED",
                    createdAt = System.currentTimeMillis(),
                    closedAt = null
                )
            )
            onComplete()
        }
    }

    class Factory(
        private val autoRepository: AutoRepository,
        private val requestRepository: RequestRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DashboardViewModel(autoRepository, requestRepository) as T
    }
}