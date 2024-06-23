package com.example.rentingproject.ui.ListScreen.Account

import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.*
import com.example.rentingproject.R
import com.example.rentingproject.ui.components.BottomNavigationBar
import com.example.rentingproject.utils.FirebaseHelper
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController, modifier: Modifier = Modifier) {
    // Current route for bottom navigation
    val currentRoute = Account.route
    val firebaseHelper = FirebaseHelper()
    var userRole by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(firebaseHelper.auth.currentUser) {
        firebaseHelper.auth.currentUser?.let { user ->
            firebaseHelper.getUserRole(user.uid) { role ->
                Timber.tag("AccountScreen").d("User Role: %s", role)
                userRole = role
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
                Image(
                    painter = painterResource(id = R.drawable.dummy_profile),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Laura Ingalls", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("+84900989278", fontSize = 16.sp)
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
