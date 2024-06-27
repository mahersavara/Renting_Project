package com.example.rentingproject.ui.ListScreen.HomeOwner.ServiceJob

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.rentingproject.NavRoute.Inbox
import com.example.rentingproject.R
import com.example.rentingproject.database.model.job.Service
import com.example.rentingproject.database.model.review.Review
import com.example.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(navController: NavController, serviceId: String, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val coroutineScope = rememberCoroutineScope()
    var service by remember { mutableStateOf<Service?>(null) }
    var isLiked by remember { mutableStateOf(false) }
    var reviews by remember { mutableStateOf(listOf<Review>()) }
    val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()

    LaunchedEffect(serviceId) {
        coroutineScope.launch {
            service = firebaseHelper.getServiceById(serviceId)
            val likedServices = firebaseHelper.getLikedServices(uid)
            isLiked = likedServices.any { it.id == serviceId }
            reviews = firebaseHelper.getReviewsForService(serviceId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Service Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            if (isLiked) {
                                firebaseHelper.removeLikedService(uid, serviceId)
                            } else {
                                service?.let { firebaseHelper.addLikedService(uid, it) }
                            }
                            isLiked = !isLiked
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = if (isLiked) R.drawable.ic_liked else R.drawable.ic_like),
                            contentDescription = "Like Button"
                        )
                    }
                }
            )
        }
    ) {
        service?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Image(
                    painter = rememberImagePainter(data = it.images.firstOrNull()),
                    contentDescription = "Service Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Location: ${it.location}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "\$${it.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Rating",
                        tint = Color.Yellow
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${it.rating} (${it.popularity})", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = it.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("chooseDate/${it.id}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Book now")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val participants = listOf(it.userId, uid)
                            val conversationId = firebaseHelper.getOrCreateConversationId(participants)
                            navController.navigate(Inbox.createRoute(conversationId, participants)) // Navigate to chat screen
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Chat with Cleaner")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reviews) { review ->
                        ReviewCard(review = review)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(data = review.userImage),
                    contentDescription = "User Image",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = review.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(review.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.reviewText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
