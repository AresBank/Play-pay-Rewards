package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val creatorId: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String = "",
    val title: String,
    val description: String,
    val videoUrl: String,
    val coverDrawableResName: String,
    val rewardCoins: Int = 500, // Mínimo 500 monedas = $5.00 MXN netos por video
    val rewardMxnNet: Double = 5.0, // $5.00 MXN netos garantizados por video visto
    val category: String, // Tech & AI, Crypto, Gaming, Comedia, Estilo de Vida
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean = false,
    val isFollowed: Boolean = false,
    val soundTitle: String = "CyberBeat • Sonido Original",
    val aiAffinityScore: Int = 98,
    val aiReason: String = "Recomendado por tu interés en tecnología",
    val createdAt: Long = System.currentTimeMillis()
)
