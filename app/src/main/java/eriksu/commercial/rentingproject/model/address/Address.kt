package eriksu.commercial.rentingproject.model.address

data class Address(
    var id: String = "",
    var userId: String = "",
    var name: String = "",
    var phoneNumber: String = "",
    var street: String = "",
    var city: String = "",
    var country: String = "",
    var isDefault: Boolean = false
)
