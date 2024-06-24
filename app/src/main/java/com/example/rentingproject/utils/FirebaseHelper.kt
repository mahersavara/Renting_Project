package com.example.rentingproject.utils

import android.net.Uri
import android.util.Log
import com.example.rentingproject.ui.ListScreen.Cleaner.homescreen.Listing
import com.example.rentingproject.ui.ListScreen.Cleaner.homescreen.Request
import com.example.rentingproject.ui.ListScreen.HomeOwner.homescreen.Service
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseHelper {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()


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
            val document = db.collection("addresses").document(uid).get().await()
            document.getString("address") ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun getPopularServices(): List<Service> {
        return try {
            val snapshot = db.collection("services").orderBy("popularity").limit(10).get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Service::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getListings(uid: String): List<Listing> {
        return try {
            val snapshot = db.collection("listings").whereEqualTo("cleanerId", uid).get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Listing::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRequests(uid: String): List<Request> {
        return try {
            val snapshot = db.collection("requests").whereEqualTo("cleanerId", uid).get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Request::class.java)
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

}
