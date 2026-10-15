package com.example.autoservice.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autos")
data class Auto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plateNumber: String,
    val brandModel: String,
    val vin: String,
    val mileage: Int,
    val ownerName: String,
    val ownerPhone: String
)