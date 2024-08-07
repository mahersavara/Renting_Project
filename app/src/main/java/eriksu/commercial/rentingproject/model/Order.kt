// Order.kt
package eriksu.commercial.rentingproject.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val serviceId: String = "",
    val date: String = "",
    val address: String = "",
    val paymentMethod: String = "",
    val status: String = "pending"
)
