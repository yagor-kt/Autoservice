package com.example.autoservice.ui.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.autoservice.data.Auto
import com.example.autoservice.data.AutoRepository
import com.example.autoservice.data.Reminder
import com.example.autoservice.data.ReminderRepository
import com.example.autoservice.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RemindersUiState(
    val reminders: List<Reminder> = emptyList(),
    val autos: List<Auto> = emptyList()
)

class RemindersViewModel(
    private val autoRepository: AutoRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    val uiState = combine(
        reminderRepository.getAll(),
        autoRepository.getAll()
    ) { reminders, autos ->
        RemindersUiState(reminders = reminders, autos = autos)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        RemindersUiState()
    )

    fun addReminder(auto: Auto, dateText: String, onComplete: (Boolean) -> Unit) {
        val dueDate = DateUtils.parseDate(dateText)
        if (dueDate == null) {
            onComplete(false)
            return
        }

        viewModelScope.launch {
            reminderRepository.insert(
                Reminder(
                    autoId = auto.id,
                    plateNumber = auto.plateNumber,
                    brandModel = auto.brandModel,
                    dueDate = dueDate
                )
            )
            onComplete(true)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.delete(reminder)
        }
    }

    class Factory(
        private val autoRepository: AutoRepository,
        private val reminderRepository: ReminderRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RemindersViewModel(autoRepository, reminderRepository) as T
    }
}