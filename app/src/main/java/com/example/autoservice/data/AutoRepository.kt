package com.example.autoservice.data

import kotlinx.coroutines.flow.Flow

class AutoRepository(
    private val dao: AutoDao
) {
    fun getAll(): Flow<List<Auto>> = dao.getAll()

    fun getById(id: Long): Flow<Auto?> = dao.getById(id)

    suspend fun insert(auto: Auto): Long = dao.insert(auto)

    suspend fun update(auto: Auto) = dao.update(auto)

    suspend fun delete(auto: Auto) = dao.delete(auto)
}