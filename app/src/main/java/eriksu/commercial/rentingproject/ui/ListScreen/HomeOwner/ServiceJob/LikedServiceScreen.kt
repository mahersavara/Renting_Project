package com.example.rentingproject.ui.ListScreen.HomeOwner.ServiceJob

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import eriksu.commercial.rentingproject.NavRoute.Liked
import eriksu.commercial.rentingproject.NavRoute.ServiceDetail
import eriksu.commercial.rentingproject.R
import eriksu.commercial.rentingproject.model.job.Service
import eriksu.commercial.rentingproject.ui.components.BottomNavigationBar
import eriksu.commercial.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedServiceScreen(navController: NavController, modifier: Modifier = Modifier) {
    val currentRoute = Liked.route
    val firebaseHelper = FirebaseHelper()
    val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()
    var likedServices by remember { mutableStateOf(listOf<Service>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            likedServices = firebaseHelper.getLikedServices(uid)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Yêu thích") },
                actions = {
                    IconButton(onClick = { /* Handle Filter */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = "Lọc",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute, userRole = "HomeOwner")
        }
    ) { pd ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(pd)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(likedServices.size) { index ->
                    LikedServiceCard(service = likedServices[index], navController = navController)
                }
            }
        }
    }
}

@Composable
fun LikedServiceCard(service: Service, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle Click */ navController.navigate(ServiceDetail.createRoute(service.id)) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = service.images.firstOrNull() ?: R.drawable.cleaner_sample),
                contentDescription = "Hình ảnh dịch vụ",
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
                    Image(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Đánh giá",
                        modifier = Modifier.size(16.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Yellow)
                    )
                    Text(text = service.rating.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
