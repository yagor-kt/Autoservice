package com.example.autoservice.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "requests")
data class Request(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val autoId: Long,
    val plateNumber: String,
    val brandModel: String,
    val ownerName: String,
    val status: String,
    val createdAt: Long,
    val closedAt: Long?
)