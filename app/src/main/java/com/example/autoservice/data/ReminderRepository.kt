package com.example.autoservice.data

import kotlinx.coroutines.flow.Flow

class ReminderRepository(
    private val dao: ReminderDao
) {
    fun getAll(): Flow<List<Reminder>> = dao.getAll()

    suspend fun insert(reminder: Reminder): Long = dao.insert(reminder)

    suspend fun delete(reminder: Reminder) = dao.delete(reminder)
}