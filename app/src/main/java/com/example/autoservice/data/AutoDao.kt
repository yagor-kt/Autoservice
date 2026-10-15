package com.example.autoservice.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AutoDao {
    @Query("SELECT * FROM autos ORDER BY brandModel ASC")
    fun getAll(): Flow<List<Auto>>

    @Query("SELECT * FROM autos WHERE id = :id")
    fun getById(id: Long): Flow<Auto?>

    @Insert
    suspend fun insert(auto: Auto): Long

    @Update
    suspend fun update(auto: Auto)

    @Delete
    suspend fun delete(auto: Auto)
}