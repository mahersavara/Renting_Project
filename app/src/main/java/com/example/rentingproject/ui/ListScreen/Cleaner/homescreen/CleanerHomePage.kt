package com.example.rentingproject.ui.ListScreen.Cleaner.homescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.rentingproject.NavRoute.*
import com.example.rentingproject.R
import com.example.rentingproject.database.model.Order
import com.example.rentingproject.database.model.job.Service
import com.example.rentingproject.ui.components.BottomNavigationBar
import com.example.rentingproject.ui.components.ServiceCard
import com.example.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanerHomePage(navController: NavController, modifier: Modifier = Modifier) {
    val currentRoute = CleanerHome.route
    val userRole = "Cleaner"
    val firebaseHelper = FirebaseHelper()
    var address by remember { mutableStateOf("") }
    var pendingOrders by remember { mutableStateOf(listOf<Order>()) }
    var services by remember { mutableStateOf(listOf<Service>()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    fun fetchData() {
        coroutineScope.launch {
            val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()
            val addresses = firebaseHelper.getUserAddresses(uid)
            val defaultAddress = addresses.find { it.isDefault }
            address = defaultAddress?.let { "${it.street}, ${it.city}, ${it.country}" } ?: "No default address set"
            pendingOrders = firebaseHelper.getPendingOrdersForCleaner(uid)
            services = firebaseHelper.getServices(uid)
            isLoading = false
        }
    }

    fun handleOrderAction(orderId: String, status: String) {
        coroutineScope.launch {
            firebaseHelper.updateOrderStatus(orderId, status) { success ->
                if (success) {
                    fetchData() // refresh data after update
                } else {
                    // handle error
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchData()
    }

    // Add this to refetch data when coming back to the screen
    navController.addOnDestinationChangedListener { _, destination, _ ->
        if (destination.route == CleanerHome.route) {
            fetchData()
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute, userRole = userRole)
        }
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                // Address Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Address: $address", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { navController.navigate(MyAddress.route) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Edit Address"
                        )
                    }
                }

                // Booking Info
                Text(
                    text = "My Address is at $address",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Requests Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Requests (${pendingOrders.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = { navController.navigate(MyJob.route) }) {
                        Text(text = "All")
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(pendingOrders.size) { index ->
                        PendingOrderItem(order = pendingOrders[index]) { orderId, status ->
                            handleOrderAction(orderId, status)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Services Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Your Listings",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = { navController.navigate(AllJobs.route) }) {
                        Text(text = "All")
                    }
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(services.size) { index ->
                        ServiceCard(navController = navController, service = services[index])
                    }
                }
            }
        }
    }
}

@Composable
fun PendingOrderItem(order: Order, onAction: (String, String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "Order from: ${order.userId}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Service: ${order.serviceId}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Date: ${order.date}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Address: ${order.address}", style = MaterialTheme.typography.bodyMedium)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { onAction(order.id, "accepted") }) {
                Text("Accept")
            }
            Button(onClick = { onAction(order.id, "cancelled") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Reject")
            }
        }
    }
}