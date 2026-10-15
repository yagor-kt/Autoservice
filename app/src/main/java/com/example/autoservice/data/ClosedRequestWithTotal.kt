package com.example.autoservice.data

data class ClosedRequestWithTotal(
    val id: Long,
    val autoId: Long,
    val plateNumber: String,
    val brandModel: String,
    val ownerName: String,
    val status: String,
    val createdAt: Long,
    val closedAt: Long?,
    val totalPrice: Int
)