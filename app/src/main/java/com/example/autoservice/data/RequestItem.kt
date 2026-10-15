package com.example.autoservice.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "request_items")
data class RequestItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requestId: Long,
    val type: String,
    val title: String,
    val price: Int
)