package com.example.rentingproject.database.model.message

data class Message(
    val id: Int = 0,
    val sender: String = "",
    val content: String = "",
    val timestamp: String = "",
    val isRead: Boolean = false,
    val avatar: Int = 0
)