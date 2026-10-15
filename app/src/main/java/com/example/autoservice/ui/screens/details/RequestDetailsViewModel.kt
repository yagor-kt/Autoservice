package com.example.autoservice.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.autoservice.data.Auto
import com.example.autoservice.data.AutoRepository
import com.example.autoservice.data.Request
import com.example.autoservice.data.RequestItem
import com.example.autoservice.data.RequestItemRepository
import com.example.autoservice.data.RequestRepository
import com.example.autoservice.util.StatusMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RequestDetailsUiState(
    val request: Request? = null,
    val auto: Auto? = null,
    val items: List<RequestItem> = emptyList(),
    val total: Int = 0,
    val loading: Boolean = true
)

class RequestDetailsViewModel(
    private val requestId: Long,
    private val autoRepository: AutoRepository,
    private val requestRepository: RequestRepository,
    private val itemRepository: RequestItemRepository
) : ViewModel() {
    private val refresh = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = refresh.flatMapLatest {
        requestRepository.getById(requestId).flatMapLatest { request ->
            if (request == null) {
                flowOf(RequestDetailsUiState(loading = false))
            } else {
                val autoFlow = if (request.autoId > 0) {
                    autoRepository.getById(request.autoId)
                } else {
                    flowOf(null)
                }
                combine(
                    autoFlow,
                    itemRepository.getByRequestId(requestId)
                ) { auto, items ->
                    RequestDetailsUiState(
                        request = request,
                        auto = auto,
                        items = items,
                        total = items.sumOf { it.price },
                        loading = false
                    )
                }
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        RequestDetailsUiState()
    )

    fun addItem(type: String, title: String, price: Int) {
        viewModelScope.launch {
            itemRepository.insert(
                RequestItem(
                    requestId = requestId,
                    type = type,
                    title = title,
                    price = price
                )
            )
            refresh.value += 1
        }
    }

    fun moveToNextStage(request: Request) {
        val nextStatus = StatusMapper.next(request.status) ?: return
        viewModelScope.launch {
            requestRepository.update(
                request.copy(
                    status = nextStatus,
                    closedAt = if (nextStatus == "CLOSED") System.currentTimeMillis() else null
                )
            )
        }
    }

    fun closeRequest(request: Request) {
        viewModelScope.launch {
            requestRepository.update(
                request.copy(
                    status = "CLOSED",
                    closedAt = System.currentTimeMillis()
                )
            )
        }
    }

    class Factory(
        private val requestId: Long,
        private val autoRepository: AutoRepository,
        private val requestRepository: RequestRepository,
        private val itemRepository: RequestItemRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RequestDetailsViewModel(
                requestId,
                autoRepository,
                requestRepository,
                itemRepository
            ) as T
    }
}