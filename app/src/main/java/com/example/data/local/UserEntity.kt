package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long = 1L,
    val authProvider: String = "email", // email, facebook, tiktok
    val email: String = "usuario@tiktokearn.io",
    val displayName: String = "Oscar Bueno",
    val username: String = "CiberCreador_MX",
    val bio: String = "Explorando la frontera cyberpunk & ganando recompensas diarias con videos ⚡🎧",
    val avatarPreset: String = "neon_cyan", // neon_cyan, neon_magenta, warm_gold, emerald_green, cyber_purple
    val coinBalance: Long = 5000L,
    val usdBalance: Double = 27.77, // corresponds to $500 MXN (18:1 rate)
    val mxnBalance: Double = 500.0, // Welcome bonus of $500 MXN pre-credited!
    val hasReceivedWelcomeBonus: Boolean = true,
    val userRankNumber: Int = 482190,
    val followersCount: Int = 1420,
    val followingCount: Int = 2,
    val referralCode: String = "PLAY-7821",
    val referralsCount: Int = 0,
    val referralEarningsMxn: Double = 0.0
)
