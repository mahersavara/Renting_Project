package com.example.rentingproject.ui.ListScreen.HomeOwner

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.rentingproject.NavRoute.ServiceDetail
import com.example.rentingproject.R
import com.example.rentingproject.database.model.job.Service
import com.example.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch

@Composable
fun ServiceCard(navController: NavController, service: Service) {
    val firebaseHelper = FirebaseHelper()
    val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()
    var isLiked by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Check if the service is already liked
    LaunchedEffect(Unit) {
        val likedServices = firebaseHelper.getLikedServices(uid)
        isLiked = likedServices.any { it.id == service.id }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate(ServiceDetail.createRoute(service.id)) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = service.images.firstOrNull()),
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
            IconButton(onClick = {
                coroutineScope.launch {
                    if (isLiked) {
                        firebaseHelper.removeLikedService(uid, service.id)
                    } else {
                        firebaseHelper.addLikedService(uid, service)
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
    }
}