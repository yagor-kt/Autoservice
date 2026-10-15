package com.example.autoservice.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class CountByTitle(
    val title: String,
    val count: Int
)

@Dao
interface RequestItemDao {
    @Query("SELECT * FROM request_items WHERE requestId = :requestId ORDER BY id ASC")
    fun getByRequestId(requestId: Long): Flow<List<RequestItem>>

    @Insert
    suspend fun insert(item: RequestItem): Long

    @Delete
    suspend fun delete(item: RequestItem)

    @Query("SELECT SUM(price) FROM request_items WHERE requestId = :requestId")
    fun sumByRequestId(requestId: Long): Flow<Int?>

    @Query(
        """
        SELECT title, COUNT(*) AS count
        FROM request_items
        WHERE type = 'WORK'
        GROUP BY title
        ORDER BY count DESC
        LIMIT 3
        """
    )
    fun topWorks(): Flow<List<CountByTitle>>
}