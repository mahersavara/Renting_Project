package eriksu.commercial.rentingproject.model.review
data class Review(
    val orderId: String = "",
    val serviceId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImage: String = "",
    val rating: Float = 0f,
    val reviewText: String = "",
    val timestamp: Long = 0L
)
