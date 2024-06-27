// Order.kt
package com.example.rentingproject.database.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val serviceId: String = "",
    val date: String = "",
    val address: String = "",
    val paymentMethod: String = "",
    val status: String = "pending"
)
