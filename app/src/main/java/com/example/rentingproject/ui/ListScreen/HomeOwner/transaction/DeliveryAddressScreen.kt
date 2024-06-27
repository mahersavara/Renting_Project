// DeliveryAddressScreen.kt
package com.example.rentingproject.ui.ListScreen.HomeOwner.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.MyAddressDetail
import com.example.rentingproject.NavRoute.PaymentBooking
import com.example.rentingproject.R
import com.example.rentingproject.database.model.address.Address
import com.example.rentingproject.utils.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryAddressScreen(navController: NavController, serviceId: String, date: String, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var addresses by remember { mutableStateOf(listOf<Address>()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            addresses = firebaseHelper.getUserAddresses(currentUserId)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Select Address") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("${MyAddressDetail.route}/new") }) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = "Add new address")
            }
        }
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                items(addresses.size) { index ->
                    Text(
                        text = addresses[index].street,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                navController.navigate(PaymentBooking.createRoute(serviceId, date, addresses[index].street))
                            },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
