package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_payment_methods")
data class PaymentMethodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long = 1L,
    val methodType: String, // MERCADO_PAGO or PAYPAL
    val fullName: String,
    val clabeOrAccount: String, // 18-digit CLABE or phone
    val email: String,
    val phone: String = "",
    val isDefault: Boolean = true
)
