package com.example.autoservice.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val autoId: Long,
    val plateNumber: String,
    val brandModel: String,
    val dueDate: Long
)