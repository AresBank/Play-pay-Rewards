package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "referrals")
data class ReferralEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val referrerUserId: Long = 1L,
    val friendName: String,
    val friendEmail: String,
    val bonusMxn: Double = 200.0,
    val status: String = "ACREDITADO", // ACREDITADO, PENDIENTE
    val registeredAt: Long = System.currentTimeMillis()
)
