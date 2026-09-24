package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_views")
data class VideoViewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val videoId: String,
    val watchTimeSeconds: Int,
    val coinsEarned: Boolean,
    val viewedAt: Long = System.currentTimeMillis()
)
