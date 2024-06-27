// OrderSuccessScreen.kt
package com.example.rentingproject.ui.ListScreen.HomeOwner.transaction

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.HomeOwnerHome
import com.example.rentingproject.R
@Composable
fun OrderSuccessScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.ic_success), contentDescription = "Success", modifier = Modifier.size(100.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text("Thank You", style = MaterialTheme.typography.headlineMedium)
        Text("For Your Booking", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = { /* View on Calendar */ }) {
                Text("View on Calendar")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = { navController.popBackStack(HomeOwnerHome.route, inclusive = false) }) {
                Text("Back to Home")
            }
        }
    }
}
