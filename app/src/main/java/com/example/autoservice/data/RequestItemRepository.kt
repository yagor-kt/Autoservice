package com.example.autoservice.data

import kotlinx.coroutines.flow.Flow

class RequestItemRepository(
    private val dao: RequestItemDao
) {
    fun getByRequestId(requestId: Long): Flow<List<RequestItem>> =
        dao.getByRequestId(requestId)

    fun sumByRequestId(requestId: Long): Flow<Int?> =
        dao.sumByRequestId(requestId)

    fun topWorks(): Flow<List<CountByTitle>> = dao.topWorks()

    suspend fun insert(item: RequestItem): Long = dao.insert(item)

    suspend fun delete(item: RequestItem) = dao.delete(item)
}