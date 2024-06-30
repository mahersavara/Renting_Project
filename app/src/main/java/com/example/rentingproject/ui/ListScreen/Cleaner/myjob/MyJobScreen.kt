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
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.rentingproject.NavRoute.Inbox
import com.example.rentingproject.NavRoute.MyJob
import com.example.rentingproject.R
import com.example.rentingproject.database.model.Order
import com.example.rentingproject.ui.components.BottomNavigationBar
import com.example.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch
import timber.log.Timber
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyJobScreen(navController: NavController, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val coroutineScope = rememberCoroutineScope()
    var pendingOrders by remember { mutableStateOf(listOf<Order>()) }
    var isLoading by remember { mutableStateOf(true) }
    val userRole = "Cleaner"

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()
            pendingOrders = firebaseHelper.getPendingOrdersForCleaner(uid)
            isLoading = false
        }
    }

    fun handleOrderAction(orderId: String, status: String) {
        coroutineScope.launch {
            firebaseHelper.updateOrderStatus(orderId, status) { success ->
                if (success) {
                    pendingOrders = pendingOrders.filter { it.id != orderId }
                } else {
                    // handle error
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Yêu cầu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = MyJob.route, userRole = userRole)
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
                    .padding(16.dp)
            ) {
                items(pendingOrders.size) { index ->
                    PendingOrderItem(order = pendingOrders[index], navController, firebaseHelper) { orderId, status ->
                        handleOrderAction(orderId, status)
                    }
                }
            }
        }
    }
}




@Composable
fun PendingOrderItem(order: Order, navController: NavController, firebaseHelper: FirebaseHelper, onAction: (String, String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val currentUserId = firebaseHelper.auth.currentUser?.uid.orEmpty()
    var serviceName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(order.serviceId) {
        coroutineScope.launch {
            serviceName = firebaseHelper.getServiceNameById(order.serviceId)
        }
    }

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
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Dịch vụ: $serviceName", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Ngày: ${order.date}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Địa chỉ: ${order.address}", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.width(16.dp))
                if (order.status == "pending") {
                    Icon(painter = painterResource(id = R.drawable.ic_dot), contentDescription = "Yêu cầu mới", tint = Color.Blue)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { onAction(order.id, "accepted") }) {
                    Text(text = "Chấp nhận")
                }
                Button(onClick = { onAction(order.id, "cancelled") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text(text = "Từ chối")
                }
                Button(onClick = {
                    coroutineScope.launch {
                        val participants = listOf(order.userId, currentUserId)
                        val conversationId = firebaseHelper.getOrCreateConversationId(participants)
                        navController.navigate(Inbox.createRoute(conversationId, participants)) // Điều hướng đến màn hình chat
                    }
                }) {
                    Text(text = "Chat")
                }
            }
        }
    }
}
