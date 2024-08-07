package eriksu.commercial.rentingproject.model.job

data class Listing(
    val serviceName: String = "",
    val location: String = "",
    val price: String = "",
    val rating: Double = 0.0,
    val images: List<String> = emptyList()
)
