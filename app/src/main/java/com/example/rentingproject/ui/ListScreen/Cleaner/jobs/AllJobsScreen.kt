package com.example.rentingproject.ui.ListScreen.Cleaner.jobs

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.rentingproject.NavRoute.AllJobs
import com.example.rentingproject.NavRoute.EditJob
import com.example.rentingproject.NavRoute.PostJob
import com.example.rentingproject.R
import com.example.rentingproject.ui.components.BottomNavItem
import com.example.rentingproject.ui.components.BottomNavigationBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllJobsScreen(navController: NavController, modifier: Modifier = Modifier) {
    val jobs = remember { mutableStateListOf(
        Listing("Cleaning", "Location", "$40", 4.9),
        Listing("Grooming", "Location", "$40", 4.9)
    ) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Your listing Jobs") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = AllJobs.route)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(PostJob.route) }) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = "Add")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(jobs.size) { index ->
                JobItem(navController = navController, job = jobs[index])
            }
        }
    }
}


@Composable
fun JobItem(navController: NavController, job: Listing) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate(EditJob.createRoute(job.serviceName)) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.cleaner_sample), // Replace with actual job image
                contentDescription = "Job Image",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = job.serviceName, style = MaterialTheme.typography.bodyLarge)
                Text(text = job.location, style = MaterialTheme.typography.bodySmall)
                Text(text = job.price, style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.ic_star), contentDescription = "Rating", tint = Color.Yellow)
                    Text(text = job.rating.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}


data class Listing(
    val serviceName: String,
    val location: String,
    val price: String,
    val rating: Double
)
