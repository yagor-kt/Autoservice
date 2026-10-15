package com.example.autoservice.data

import kotlinx.coroutines.flow.Flow

class RequestRepository(
    private val dao: RequestDao
) {
    fun getAll(): Flow<List<Request>> = dao.getAll()

    fun getById(id: Long): Flow<Request?> = dao.getById(id)

    fun getByStatus(status: String): Flow<List<Request>> = dao.getByStatus(status)

    fun getClosed(): Flow<List<Request>> = dao.getClosed()

    fun getByAutoId(autoId: Long): Flow<List<Request>> = dao.getByAutoId(autoId)

    fun countByStatus(status: String): Flow<Int> = dao.countByStatus(status)

    fun countClosedBetween(start: Long, end: Long): Flow<Int> =
        dao.countClosedBetween(start, end)

    fun revenueClosedBetween(start: Long, end: Long): Flow<Int> =
        dao.revenueClosedBetween(start, end)

    fun topClosedBrands(): Flow<List<CountByBrand>> = dao.topClosedBrands()

    suspend fun insert(request: Request): Long = dao.insert(request)

    suspend fun update(request: Request) = dao.update(request)

    suspend fun delete(request: Request) = dao.delete(request)
}