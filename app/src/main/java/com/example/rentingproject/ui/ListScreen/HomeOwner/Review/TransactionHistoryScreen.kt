package com.example.rentingproject.ui.ListScreen.Account.TransactionHistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.LeaveReview
import com.example.rentingproject.R
import com.example.rentingproject.database.model.Order
import com.example.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(navController: NavController, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val coroutineScope = rememberCoroutineScope()
    var orders by remember { mutableStateOf(listOf<Order>()) }
    var isLoading by remember { mutableStateOf(true) }
    val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            orders = firebaseHelper.getUserOrders(uid)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Lịch sử giao dịch") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Quay lại")
                    }
                }
            )
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
                items(orders.size) { index ->
                    val order = orders[index]
                    val orderDate = SimpleDateFormat("EEE MMM dd yyyy, hh:mma", Locale.getDefault()).parse(order.date)
                    val isExpired = orderDate?.before(Date()) ?: false

                    if (order.status == "accepted" && isExpired) {
                        var hasReviewed by remember { mutableStateOf(false) }

                        LaunchedEffect(order.id) {
                            coroutineScope.launch {
                                hasReviewed = firebaseHelper.hasReviewedOrder(order.id)
                            }
                        }

                        if (!hasReviewed) {
                            TransactionHistoryItem(navController, order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionHistoryItem(navController: NavController, order: Order) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate(LeaveReview.createRoute(order.id))
            }
    ) {
        Text(
            text = "Dịch vụ: ${order.serviceId}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ngày: ${order.date}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Địa chỉ: ${order.address}",
            style = MaterialTheme.typography.bodySmall
        )
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
    }
}
