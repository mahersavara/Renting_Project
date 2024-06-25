package com.example.rentingproject.ui.ListScreen.HomeOwner.homescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.*
import com.example.rentingproject.R
import com.example.rentingproject.ui.components.BottomNavItem
import com.example.rentingproject.ui.components.BottomNavigationBar
import com.example.rentingproject.utils.FirebaseHelper
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeOwnerHomepageScreen(navController: NavController, modifier: Modifier = Modifier) {
    val currentRoute = HomeOwnerHome.route
    val userRole = "HomeOwner"
    val firebaseHelper = FirebaseHelper()
    var address by remember { mutableStateOf("") }
    var popularServices by remember { mutableStateOf(listOf<Service>()) }
    var lastVisibleService by remember { mutableStateOf<QuerySnapshot?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()



    fun loadMoreServices() {
        coroutineScope.launch {
            if (!isLoading) {
                isLoading = true
                val newServices = firebaseHelper.getPopularServices(lastVisibleService)
                popularServices = popularServices + newServices
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        address = firebaseHelper.getUserAddress(firebaseHelper.auth.currentUser?.uid.orEmpty())
        loadMoreServices()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute, userRole = userRole)
        }
    ) {
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
                text = "Book A Cleaning for your House at: $address",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle Search Input */ },
                label = { Text("Search") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { /* Handle Filter */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = "Filter"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Most Popular Section
            Text(
                text = "Most popular",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(popularServices) { index, service ->
                    if (index == popularServices.size - 1 && !isLoading) {
                        loadMoreServices()
                    }
                    CleanerCard(navController = navController, service = service)
                }
            }
        }
    }
}

@Composable
fun CleanerCard(navController: NavController, service: Service) {
    var isLiked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp)
            .clickable { navController.navigate(ServiceDetail.createRoute(service.name)) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.cleaner_sample), // Replace with actual service image
                contentDescription = "Service Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = service.name, style = MaterialTheme.typography.bodyMedium)
            Text(text = service.location, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "\$${service.price}", style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Rating",
                        tint = Color.Yellow
                    )
                    Text(text = service.rating.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = { isLiked = !isLiked }) {
                Icon(
                    painter = painterResource(id = if (isLiked) R.drawable.ic_liked else R.drawable.ic_like),
                    contentDescription = "Like Button"
                )
            }
        }
    }
}

data class Service(
    val name: String,
    val location: String,
    val price: String,
    val rating: Double
)