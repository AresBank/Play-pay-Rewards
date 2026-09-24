package com.example.ui.model

data class CommentItem(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val text: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val timeAgo: String = "Hace 2h"
)
