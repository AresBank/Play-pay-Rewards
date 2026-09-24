package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payout_requests")
data class PayoutRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long = 1L,
    val amountMxn: Double,
    val amountUsd: Double,
    val coinsDeducted: Long = 0L,
    val paymentMethod: String, // MERCADO_PAGO or PAYPAL
    val destinationAccount: String,
    val status: String = "PENDING", // PENDING, PROCESSING, SUCCESS, REJECTED
    val transactionReference: String,
    val requestedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
