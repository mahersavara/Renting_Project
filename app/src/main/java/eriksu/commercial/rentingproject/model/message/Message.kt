package eriksu.commercial.rentingproject.model.message

data class Message(
    val conversationId: String = "",
    val sender: String = "",
    val content: String = "",
    var timestamp: Long = 0L, // Use Long for timestamp
    val isRead: Boolean = false,
    val avatarUrl: String = "", // URL for the user's avatar image
    val participants: List<String> = listOf()
)