package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val authorId: Long = 1L,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarPreset: String = "neon_cyan",
    val text: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isMine: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
