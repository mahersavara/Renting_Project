package com.example.rentingproject.ui.ListScreen.Account

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.rentingproject.NavRoute.*
import com.example.rentingproject.R
import com.example.rentingproject.ui.components.BottomNavigationBar
import com.example.rentingproject.utils.FirebaseHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController, modifier: Modifier = Modifier) {
    val currentRoute = Account.route
    val firebaseHelper = FirebaseHelper()
    var userRole by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf("Edit DefaultName") }
    var userPhoneNumber by remember { mutableStateOf("+84900989278") }
    var userProfilePicture by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    LaunchedEffect(firebaseHelper.auth.currentUser) {
        firebaseHelper.auth.currentUser?.let { user ->
            firebaseHelper.getUserRole(user.uid) { role ->
                Timber.tag("AccountScreen").d("User Role: %s", role)
                userRole = role
            }
            val userDocRef = Firebase.firestore.collection("users").document(user.uid)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userName = document.getString("name") ?: "Edit DefaultName"
                        userPhoneNumber = document.getString("phoneNumber") ?: "+84900989278"
                        userProfilePicture = document.getString("profilePicture")?.let { Uri.parse(it) }
                    } else {
                        Timber.tag("AccountScreen").d("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.tag("AccountScreen").e(exception, "get failed with ")
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (userProfilePicture != null) {
                    Image(
                        painter = rememberImagePainter(userProfilePicture),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_me),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(userPhoneNumber, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit Profile",
                    modifier = Modifier
                        .size(64.dp)
                        .clickable {
                            navController.navigate(PersonalInfo.route)
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Verify your email for enhanced account protection.", modifier = Modifier.weight(1f))
                    Button(onClick = { /* Handle Email Verification */ }) {
                        Text("Verify")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (userRole == "HomeOwner") {
                ListItem("Transaction history", id = R.drawable.ic_trans_history)
            }

            ListItem("Privacy & Security", id = R.drawable.ic_privacy)
            ListItem("Payment Method", id = R.drawable.ic_payment_method)
            ListItem("Address", id = R.drawable.ic_address)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    /* Handle Logout */
                    navController.navigate(Login.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log out")
            }

            Spacer(modifier = Modifier.weight(1f))
            userRole?.let { BottomNavigationBar(navController, currentRoute, it) }
        }
    }
}

@Composable
fun ListItem(text: String, @DrawableRes id: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* Handle item click */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Icon(painter = painterResource(id = id), contentDescription = null)
    }
}
