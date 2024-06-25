package com.example.rentingproject.database.model.message

data class InboxItem(
    val id: Int = 0,
    val name: String = "",
    val lastMessage: String = "",
    val timestamp: String = "",
    val isRead: Boolean = false,
    val avatar: Int = 0
)
