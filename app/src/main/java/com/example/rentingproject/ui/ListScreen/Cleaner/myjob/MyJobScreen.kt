package com.example.rentingproject.ui.ListScreen.Cleaner.myjob

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.rentingproject.NavRoute.MyJob
import com.example.rentingproject.R
import com.example.rentingproject.ui.components.BottomNavItem
import com.example.rentingproject.ui.components.BottomNavigationBar

// This related to my requests
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyJobScreen(navController: NavController, modifier: Modifier = Modifier) {
    val requests = remember { mutableStateListOf(
        Request("Cleaning - [Address]", "Request for Sun Mar 31, 5:36pm", "1 hrs ago"),
        Request("Grooming - [Address]", "Request for Sun Mar 31, 6:00pm", "2 hrs ago"),
        Request("Cleaning - [Address]", "Request for Sun Mar 31, 5:36pm", "1 hrs ago")
    ) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Requests") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = MyJob.route)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(requests.size) { index ->
                RequestItem(request = requests[index])
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

data class Request(
    val title: String,
    val subtitle: String,
    val timestamp: String
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
