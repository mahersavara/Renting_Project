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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rentingproject.R
import com.example.rentingproject.ui.components.BottomNavItem
import com.example.rentingproject.ui.components.BottomNavigationBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CleanerHomePage(navController: NavController, modifier: Modifier = Modifier) {
    val currentRoute = "cleaner_homepage"
    val requests = remember { mutableStateListOf(
        Request("Cleaning - [Address]", "Request for Sun Mar 31, 5:36pm", "1 hrs ago"),
        Request("Grooming - [Address]", "Request for Sun Mar 31, 6:00pm", "2 hrs ago")
    ) }
    val listings = remember { mutableStateListOf(
        Listing("Cleaning", "Location", "$40", 4.9),
        Listing("Grooming", "Location", "$40", 4.9)
    ) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
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
                Text(text = "Address", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { /* Handle Address Edit */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit Address"
                    )
                }
            }

            // Booking Info
            Text(
                text = "My Address is at [Address]",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Requests Section
            Text(
                text = "Requests (${requests.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(requests.size) { index ->
                    RequestItem(request = requests[index])
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Listings Section
            Text(
                text = "Your listing",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listings.size) { index ->
                    ListingCard(navController = navController, listing = listings[index])
                }
            }
        }
    }
}

@Composable
fun RequestItem(request: Request) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.cleaner_sample), // Replace with actual user image
                    contentDescription = "Request Image",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = request.title, style = MaterialTheme.typography.bodyLarge)
                    Text(text = request.subtitle, style = MaterialTheme.typography.bodySmall)
                    Text(text = request.timestamp, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(painter = painterResource(id = R.drawable.ic_dot), contentDescription = "New Request", tint = Color.Blue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { /* Handle Accept */ }) {
                    Text(text = "Accept")
                }
                Button(onClick = { /* Handle Reject */ }) {
                    Text(text = "Reject")
                }
                Button(onClick = { /* Handle Chat */ }) {
                    Text(text = "Chat")
                }
            }
        }
    }
}

@Composable
fun ListingCard(navController: NavController, listing: Listing) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp)
            .clickable { navController.navigate("service_detail") },
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
            Text(text = listing.serviceName, style = MaterialTheme.typography.bodyMedium)
            Text(text = listing.location, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = listing.price, style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Rating",
                        tint = Color.Yellow
                    )
                    Text(text = listing.rating.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

data class Request(
    val title: String,
    val subtitle: String,
    val timestamp: String
)

data class Listing(
    val serviceName: String,
    val location: String,
    val price: String,
    val rating: Double
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem("homeowner", R.drawable.ic_home, "Home"),
        BottomNavItem("job", R.drawable.ic_job, "My Job"),
        BottomNavItem("booking", R.drawable.ic_booking, "Booking"),
        BottomNavItem("message", R.drawable.ic_message, "Message"),
        BottomNavItem("account", R.drawable.ic_me, "Me")
    )

    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val route: String, val icon: Int, val label: String)
