package com.example.autoservice.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class CountByBrand(
    val brandModel: String,
    val count: Int
)

@Dao
interface RequestDao {
    @Query("SELECT * FROM requests ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Request>>

    @Query("SELECT * FROM requests WHERE id = :id")
    fun getById(id: Long): Flow<Request?>

    @Query("SELECT * FROM requests WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: String): Flow<List<Request>>

    @Query("SELECT * FROM requests WHERE status = 'CLOSED' ORDER BY closedAt DESC")
    fun getClosed(): Flow<List<Request>>

    @Query(
        """
        SELECT
            r.id AS id,
            r.autoId AS autoId,
            r.plateNumber AS plateNumber,
            r.brandModel AS brandModel,
            r.ownerName AS ownerName,
            r.status AS status,
            r.createdAt AS createdAt,
            r.closedAt AS closedAt,
            COALESCE(SUM(ri.price), 0) AS totalPrice
        FROM requests AS r
        LEFT JOIN request_items AS ri ON ri.requestId = r.id
        WHERE r.status = 'CLOSED'
        GROUP BY r.id
        ORDER BY r.closedAt DESC
        """
    )
    fun getClosedWithTotals(): Flow<List<ClosedRequestWithTotal>>

    @Query("SELECT * FROM requests WHERE autoId = :autoId ORDER BY createdAt DESC")
    fun getByAutoId(autoId: Long): Flow<List<Request>>

    @Query("SELECT COUNT(*) FROM requests WHERE status = :status")
    fun countByStatus(status: String): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) FROM requests
        WHERE status = 'CLOSED' AND closedAt BETWEEN :start AND :end
        """
    )
    fun countClosedBetween(start: Long, end: Long): Flow<Int>

    @Insert
    suspend fun insert(request: Request): Long

    @Update
    suspend fun update(request: Request)

    @Delete
    suspend fun delete(request: Request)

    @Query(
        """
        SELECT COALESCE(SUM(ri.price), 0)
        FROM request_items AS ri
        INNER JOIN requests AS r ON r.id = ri.requestId
        WHERE r.status = 'CLOSED' AND r.closedAt BETWEEN :start AND :end
        """
    )
    fun revenueClosedBetween(start: Long, end: Long): Flow<Int>

    @Query(
        """
        SELECT r.brandModel AS brandModel, COUNT(*) AS count
        FROM requests AS r
        WHERE r.status = 'CLOSED'
        GROUP BY r.brandModel
        ORDER BY count DESC
        LIMIT 3
        """
    )
    fun topClosedBrands(): Flow<List<CountByBrand>>
}