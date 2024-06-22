package com.example.rentingproject.ui.ListScreen.Account

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
import com.example.rentingproject.NavRoute.Account
import com.example.rentingproject.NavRoute.Login
import com.example.rentingproject.NavRoute.PersonalInfo
import com.example.rentingproject.R
import com.example.rentingproject.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController, modifier: Modifier = Modifier) {
    // Current route for bottom navigation
    val currentRoute = Account.route
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
                    modifier = Modifier.size(64.dp).clickable{
                        navController.navigate(PersonalInfo.route)
                    }
                )

            }

            Spacer(modifier = Modifier.height(16.dp))


            Card(
//                colors = , // Light orange background for verification banner
                modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Verify your email for enhanced account protection.", modifier = Modifier.weight(1f))
                    Button(onClick = { /* Handle Email Verification ( Not Available ) version 1.0 */ }) {
                        Text("Verify")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ListItem("Transaction history", id = R.drawable.ic_trans_history)
            // adding address section && payment section
            ListItem("Recently viewed", id = R.drawable.ic_recent_view)
            ListItem("Setting", id = R.drawable.ic_setting)
            ListItem("Privacy & Security", id = R.drawable.ic_privacy)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                            /* TODO Handle Logout ( cho khi co logic) */
                navController.navigate(Login.route)
                             }, modifier = Modifier.fillMaxWidth()) {
                Text("Log out")
            }

            // Bottom navigation bar placeholder
            Spacer(modifier = Modifier.weight(1f))
            BottomNavigationBar(navController,currentRoute)
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

