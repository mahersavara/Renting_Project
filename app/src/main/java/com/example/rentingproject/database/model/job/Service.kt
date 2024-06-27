package com.example.rentingproject.database.model.job

import com.example.rentingproject.database.model.review.Rating
import com.example.rentingproject.database.model.review.Review

data class Service(
    var id: String = "",
    val name: String = "",
    val location: String = "",
    val price: String = "",
    val rating: Double = 0.0,
    val popularity: Int = 0,
    val description: String = "",
    val images: List<String> = emptyList(),
    val reviews: List<Review> = emptyList(),
    var ratings: List<Rating> = emptyList(),
    var userId: String = ""
)
