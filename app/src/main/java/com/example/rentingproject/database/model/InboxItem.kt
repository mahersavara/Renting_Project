package com.example.rentingproject.database.model

data class InboxItem(
    val id: Int,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val isRead: Boolean,
    val avatar: Int // Resource ID for the avatar image
)