package com.example.rentingproject.database.model.review

data class Review(
    val reviewerName: String = "",
    val reviewerProfileImage: String = "",
    val reviewDate: String = "",
    val reviewContent: String = "",
    val rating: Double = 0.0
)