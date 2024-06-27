package com.example.rentingproject.utils

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.example.rentingproject.database.model.Order
import com.example.rentingproject.database.model.address.Address
import com.example.rentingproject.database.model.job.Listing
import com.example.rentingproject.database.model.job.Service
import com.example.rentingproject.database.model.message.Message
import com.example.rentingproject.database.model.review.Review
import com.example.rentingproject.ui.ListScreen.HomeOwner.MessageFlow.Conversation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseHelper {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val servicesCollection = db.collection("services")


    init {
        // Enable Firestore offline persistence and ensure it is not in offline mode
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
    }

    fun registerUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun getUserRole(uid: String, onComplete: (String?) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val role = document.getString("role")
                    Timber.tag("FirebaseHelper").d("Fetched user role: %s", role)
                    onComplete(role)
                } else {
                    Timber.tag("FirebaseHelper").d("Fetched user role: document null")
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                    exception ->
                Timber.tag("FirebaseHelper").e(exception, "Error fetching user role")
                onComplete(null)
            }
    }

    fun setUserRole(uid: String, role: String, onComplete: (Boolean) -> Unit) {
        val userRoleData = hashMapOf(
            "role" to role
        )

        db.collection("users").document(uid).set(userRoleData)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }


    suspend fun getUserAddress(uid: String): String {
        return try {
            val document = db.collection("users").document(uid).get().await()
            document.getString("address") ?: ""
        } catch (e: Exception) {
            Timber.e(e, "Error fetching address")
            ""
        }
    }

//    suspend fun getServices(lastVisibleService: DocumentSnapshot?): List<Service> {
//        return try {
//            val query = if (lastVisibleService == null) {
//                db.collection("services").limit(10)
//            } else {
//                db.collection("services").startAfter(lastVisibleService).limit(10)
//            }
//            val snapshot = query.get().await()
//            snapshot.documents.mapNotNull { document ->
//                document.toObject(Service::class.java)?.copy(snapshot = document)
//            }
//        } catch (e: Exception) {
//            Timber.tag("FirebaseHelper").e(e, "Error fetching services")
//            emptyList()
//        }
//    }





    suspend fun getPopularServices(lastVisible: QuerySnapshot? = null): List<Service> {
        return try {
            val query = db.collection("services")
                .orderBy("popularity")
                .limit(10)
            val snapshot = lastVisible?.let {
                query.startAfter(it.documents.lastOrNull()).get().await()
            } ?: query.get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Service::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }










    fun uploadProfilePicture(uid: String, uri: Uri, onComplete: (String?) -> Unit) {
        val ref = storage.reference.child("profile_pictures/$uid.jpg")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    db.collection("users").document(uid).update("profilePicture", downloadUri.toString())
                        .addOnSuccessListener {
                            onComplete(downloadUri.toString())
                        }
                        .addOnFailureListener {
                            onComplete(null)
                        }
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }













// get cleanner services


    suspend fun getListings(uid: String): List<Listing> {
        return try {
            val snapshot = db.collection("listings").whereEqualTo("cleanerId", uid).get().await()
            snapshot.documents.mapNotNull { it.toObject(Listing::class.java) }
        } catch (e: Exception) {
            Timber.tag("FirebaseHelper").e(e, "Error fetching listings")
            emptyList()
        }
    }


    // Handle Crud service
    fun postService(service: Service, onComplete: (Boolean) -> Unit) {
        val newServiceRef = db.collection("services").document()
        service.id = newServiceRef.id
        service.userId = auth.currentUser?.uid.orEmpty()
        newServiceRef.set(service)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


    fun editService(service: Service, onComplete: (Boolean) -> Unit) {
        db.collection("services").document(service.id).set(service)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteService(serviceId: String, onComplete: (Boolean) -> Unit) {
        db.collection("services").document(serviceId).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun uploadServiceImage(serviceId: String, uri: Uri, onComplete: (String?) -> Unit) {
        val ref = storage.reference.child("service_images/$serviceId.jpg")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    onComplete(downloadUri.toString())
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }


    suspend fun getServices(uid: String): List<Service> {
        return try {
            val snapshot = db.collection("services").whereEqualTo("userId", uid).get().await()
            snapshot.documents.mapNotNull { it.toObject(Service::class.java) }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching services")
            emptyList()
        }
    }


    //Address section.


    suspend fun getAddressById(addressId: String): Address? {
        return try {
            val document = db.collection("users").document(auth.currentUser?.uid.orEmpty()).collection("addresses").document(addressId).get().await()
            document.toObject(Address::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching address by ID")
            null
        }
    }

    fun addAddress(uid: String, address: Address, onComplete: (Boolean) -> Unit) {
        val newAddressRef = db.collection("users").document(uid).collection("addresses").document()
        address.id = newAddressRef.id
        newAddressRef.set(address)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateAddress(address: Address, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(auth.currentUser?.uid.orEmpty()).collection("addresses").document(address.id).set(address)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


    fun updateAddresses(addresses: List<Address>) {
        val batch = db.batch()
        val uid = auth.currentUser?.uid.orEmpty()
        addresses.forEach { address ->
            val docRef = db.collection("users").document(uid).collection("addresses").document(address.id)
            batch.set(docRef, address)
        }
        batch.commit()
            .addOnSuccessListener {
                Timber.d("Addresses updated successfully")
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error updating addresses")
            }
    }

    fun deleteAddress(addressId: String, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(auth.currentUser?.uid.orEmpty()).collection("addresses").document(addressId).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


    // !HomeOwner Role

// another way
    suspend fun getDefaultAddress(uid: String): String {
        return try {
            val snapshot = db.collection("addresses")
                .whereEqualTo("userId", uid)
                .whereEqualTo("isDefault", true)
                .get()
                .await()
            if (snapshot.documents.isNotEmpty()) {
                snapshot.documents.first().getString("address") ?: ""
            } else {
                "No default address found"
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching default address")
            ""
        }
    }


    suspend fun getMoreServices(lastVisible: DocumentSnapshot? = null, limit: Long = 10): List<Pair<Service, DocumentSnapshot>> {
        return try {
            val query = if (lastVisible == null) {
                db.collection("services").limit(limit)
            } else {
                db.collection("services").startAfter(lastVisible).limit(limit)
            }

            val snapshot = query.get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Service::class.java)?.let { service ->
                    service.id = document.id
                    Pair(service, document)
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }



    suspend fun addLikedService(userId: String, service: Service) {
        db.collection("users").document(userId)
            .collection("likedServices").document(service.id).set(service).await()
    }


    suspend fun removeLikedService(userId: String, serviceId: String) {
        db.collection("users").document(userId)
            .collection("likedServices").document(serviceId).delete().await()
    }


    suspend fun getLikedServices(userId: String): List<Service> {
        return try {
            val documents = db.collection("users").document(userId)
                .collection("likedServices").get().await()
            documents.map { it.toObject(Service::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getServiceById(serviceId: String): Service? {
        return try {
            val document = db.collection("services").document(serviceId).get().await()
            document.toObject(Service::class.java)
        } catch (e: Exception) {
            null
        }
    }



// conversation



//    suspend fun getOrCreateConversationWithUser(userId: String): String {
//        val currentUser = auth.currentUser?.uid.orEmpty()
//        val querySnapshot = db.collection("conversations")
//            .whereEqualTo("participants", listOf(currentUser, userId).sorted())
//            .get()
//            .await()
//
//        return if (querySnapshot.isEmpty) {
//            val newConversationRef = db.collection("conversations").document()
//            val newConversation = hashMapOf(
//                "participants" to listOf(currentUser, userId).sorted(),
//                "lastMessage" to "",
//                "lastTimestamp" to System.currentTimeMillis()
//            )
//            newConversationRef.set(newConversation).await()
//            newConversationRef.id
//        } else {
//            querySnapshot.documents.first().id
//        }
//    }

    fun markMessageAsRead(conversationId: String) {
        db.collection("conversations").document(conversationId).update("isRead", true)
            .addOnSuccessListener {
                Timber.tag("FirebaseHelper").d("Message marked as read")
            }
            .addOnFailureListener { exception ->
                Timber.tag("FirebaseHelper").e(exception, "Error marking message as read")
            }
    }




    // todo, i dont see

    @OptIn(DelicateCoroutinesApi::class)
    fun getConversationsForUser(userId: String, onConversationsFetched: (List<Conversation>) -> Unit) {
        db.collection("messages")
            .whereArrayContains("participants", userId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e(e, "Listen for conversations failed")
                    return@addSnapshotListener
                }

                GlobalScope.launch {
                    val conversations = snapshot?.documents?.mapNotNull { document ->
                        val message = document.toObject(Message::class.java)
                        message?.let {
                            val otherUserId = it.participants.first { id -> id != userId }
                            val userDoc = db.collection("users").document(otherUserId).get().await()
                            val userName = userDoc.getString("name") ?: "Anonymous User"
                            val userAvatar = userDoc.getString("profilePicture") ?: ""
                            Conversation(
                                id = document.id,
                                name = userName,
                                lastMessage = it.content,
                                timestamp = it.timestamp,
                                isRead = it.isRead,
                                avatar = userAvatar,
                                participants = it.participants // Ensure participants are included
                            )
                        }
                    } ?: emptyList()

                    Timber.tag("FirebaseHelper").d("getConversationsForUser: $conversations")

                    onConversationsFetched(conversations)
                }
            }
    }




    // Update getMessagesForConversation to use String for conversationId
    fun listenForMessages(conversationId: String, onMessagesReceived: (List<Message>) -> Unit) {
        db.collection("messages")
            .whereEqualTo("conversationId", conversationId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e(e, "Listen failed.")
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { document ->
                    try {
                        val message = document.toObject(Message::class.java)
                        message?.apply {
                            timestamp = document.getLong("timestamp") ?: 0L
                        }
                    } catch (ex: Exception) {
                        Timber.tag("FirebaseHelper").e(ex, "Error deserializing message")
                        null
                    }
                } ?: emptyList()

                onMessagesReceived(messages)
            }
    }

    //todo fix bug, partipation
    fun sendMessage(conversationId: String, content: String, participants: List<String>) {
        val currentUser = auth.currentUser ?: return
        val newMessage = hashMapOf(
            "conversationId" to conversationId,
            "sender" to currentUser.uid,
            "content" to content,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false,
            "avatarUrl" to currentUser.photoUrl.toString(),
            "participants" to participants
        )

        db.collection("messages").add(newMessage)
            .addOnSuccessListener {
                db.collection("conversations").document(conversationId).update(
                    mapOf(
                        "lastMessage" to content,
                        "isRead" to false,
                        "timestamp" to System.currentTimeMillis(),
                        "participants" to participants
                    )
                )
                Timber.tag("FirebaseHelper").d("Message sent successfully")
            }
            .addOnFailureListener { exception ->
                Timber.tag("FirebaseHelper").e(exception, "Error sending message")
            }
    }


    suspend fun getMessagesForConversation(userId: String, onMessagesFetched: (List<Message>) -> Unit) {
        db.collection("messages")
            .whereEqualTo("conversationId", userId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.tag("FirebaseHelper").e(e, "Listen for messages failed")
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) }
                onMessagesFetched(messages.orEmpty())
            }
    }

    fun deleteMessage(conversationId: String, messageId: String) {
        db.collection("messages").document(messageId).delete()
            .addOnSuccessListener {
                Timber.tag("FirebaseHelper").d("Message deleted successfully")
            }
            .addOnFailureListener { exception ->
                Timber.tag("FirebaseHelper").e(exception, "Error deleting message")
            }
    }

    fun deleteConversation(conversationId: String) {
        db.collection("conversations").document(conversationId).delete()
            .addOnSuccessListener {
                Timber.tag("FirebaseHelper").d("Conversation deleted successfully")
            }
            .addOnFailureListener { exception ->
                Timber.tag("FirebaseHelper").e(exception, "Error deleting conversation")
            }
    }

    suspend fun getOrCreateConversationId(participants: List<String>): String {
        val sortedParticipants = participants.sorted()
        val conversationId = sortedParticipants.joinToString(separator = "_")

        val conversationRef = db.collection("conversations").document(conversationId)
        val conversationSnapshot = conversationRef.get().await()

        if (!conversationSnapshot.exists()) {
            conversationRef.set(mapOf("participants" to sortedParticipants)).await()
        }

        return conversationId
    }

    suspend fun getUserAddresses(uid: String): List<Address> {
        return try {
            val snapshot = db.collection("users").document(uid).collection("addresses").get().await()
            snapshot.documents.mapNotNull { it.toObject(Address::class.java) }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching addresses")
            emptyList()
        }
    }
    suspend fun saveOrder(uid: String, serviceId: String, date: String, address: String, paymentMethod: String) {
        try {
            val orderRef = db.collection("orders").document()
            val newOrder = Order(
                id = orderRef.id,
                userId = uid,
                serviceId = serviceId,
                date = date,
                address = address,
                paymentMethod = paymentMethod,
                status = "pending"
            )
            orderRef.set(newOrder).await()
        } catch (e: Exception) {
            Timber.e(e, "Error saving order")
        }
    }

    // request

    fun updateOrderStatus(orderId: String, status: String, onComplete: (Boolean) -> Unit) {
        db.collection("orders").document(orderId)
            .update("status", status)
            .addOnSuccessListener {
                Timber.d("Order status updated to $status for orderId: $orderId")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error updating order status to $status for orderId: $orderId")
                onComplete(false)
            }
    }


    suspend fun getPendingOrdersForCleaner(uid: String): List<Order> {
        Timber.d("Fetching services for cleaner with uid: $uid")
        return try {
            // Fetch services provided by the cleaner
            val servicesSnapshot = db.collection("services")
                .whereEqualTo("userId", uid)
                .get()
                .await()

            val serviceIds = servicesSnapshot.documents.mapNotNull { it.id }
            Timber.d("Fetched ${serviceIds.size} services for cleaner with uid: $uid")

            // Fetch orders for those services
            val ordersSnapshot = db.collection("orders")
                .whereIn("serviceId", serviceIds)
                .whereEqualTo("status", "pending")
                .get()
                .await()

            val orders = ordersSnapshot.documents.mapNotNull { it.toObject(Order::class.java) }

            Timber.d("Fetched ${orders.size} pending orders for cleaner with uid: $uid")
            orders.forEach { order ->
                Timber.d("Order ID: ${order.id}, Service ID: ${order.serviceId}, Date: ${order.date}, Status: ${order.status}")
            }

            orders
        } catch (e: Exception) {
            Timber.e(e, "Error fetching pending orders for cleaner with uid: $uid")
            emptyList()
        }
    }


    suspend fun getUserOrders(uid: String): List<Order> {
        return try {
            val snapshot = db.collection("orders")
                .whereEqualTo("userId", uid)
                .whereEqualTo("status", "accepted")
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching orders")
            emptyList()
        }
    }

    suspend fun hasReviewedOrder(orderId: String): Boolean {
        return try {
            val snapshot = db.collection("reviews")
                .whereEqualTo("orderId", orderId)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            Timber.e(e, "Error checking review status for order $orderId")
            false
        }
    }

    suspend fun leaveReview(userId: String, orderId: String, rating: Int, reviewText: String) {
        val order = db.collection("orders").document(orderId).get().await().toObject(Order::class.java)
        val user = db.collection("users").document(userId).get().await()

        if (order != null && user != null) {
            val review = Review(
                orderId = orderId,
                serviceId = order.serviceId,
                userId = userId,
                userName = user.getString("name") ?: "",
                userImage = user.getString("profilePicture") ?: "",
                rating = rating.toFloat(),
                reviewText = reviewText,
                timestamp = System.currentTimeMillis()
            )

            db.collection("reviews").add(review).await()
        }
    }




    suspend fun getReviewsForService(serviceId: String): List<Review> {
        return try {
            val snapshot = db.collection("reviews")
                .whereEqualTo("serviceId", serviceId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching reviews for service: $serviceId")
            emptyList()
        }
    }
    suspend fun getOrderById(orderId: String): Order? {
        return try {
            val document = db.collection("orders").document(orderId).get().await()
            document.toObject(Order::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching order with ID: $orderId")
            null
        }
    }


}
