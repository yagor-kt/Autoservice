package com.example.autoservice.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.autoservice.data.Request
import com.example.autoservice.data.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val requests: List<Request> = emptyList(),
    val query: String = ""
)

class HistoryViewModel(
    requestRepository: RequestRepository
) : ViewModel() {
    private val query = MutableStateFlow("")

    val uiState = combine(requestRepository.getClosed(), query) { requests, search ->
        val filtered = if (search.isBlank()) {
            requests
        } else {
            requests.filter {
                it.plateNumber.contains(search.trim(), ignoreCase = true)
            }
        }
        HistoryUiState(requests = filtered, query = search)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HistoryUiState()
    )

    fun updateQuery(value: String) {
        query.value = value
    }

    class Factory(
        private val requestRepository: RequestRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HistoryViewModel(requestRepository) as T
    }
}