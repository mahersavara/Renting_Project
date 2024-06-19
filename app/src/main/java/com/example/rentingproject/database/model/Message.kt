package com.example.rentingproject.database.model

data class Message(
    val id: Int,
    val sender: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean,
    val avatar: Int // Resource ID for the avatar image
)