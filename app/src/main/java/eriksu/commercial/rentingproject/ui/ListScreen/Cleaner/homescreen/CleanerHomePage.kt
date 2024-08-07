package eriksu.commercial.rentingproject.ui.ListScreen.Cleaner.homescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import eriksu.commercial.rentingproject.NavRoute.*
import eriksu.commercial.rentingproject.R
import eriksu.commercial.rentingproject.model.Order
import eriksu.commercial.rentingproject.model.job.Service
import eriksu.commercial.rentingproject.ui.components.BottomNavigationBar
import eriksu.commercial.rentingproject.ui.components.ServiceCard
import eriksu.commercial.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
            address = defaultAddress?.let { "${it.street}, ${it.city}, ${it.country}" } ?: "Chưa có địa chỉ mặc định"
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
                    Text(text = "Địa chỉ: $address", style = MaterialTheme.typography.bodyMedium, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { navController.navigate(MyAddress.route) }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Chỉnh sửa địa chỉ"
                        )
                    }
                }

                // Booking Info
                Text(
                    text = "Địa chỉ của tôi tại $address",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
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
                        text = "Yêu cầu (${pendingOrders.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp
                    )
                    TextButton(onClick = { navController.navigate(MyJob.route) }) {
                        Text(text = "Tất cả")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
//                        .height(200.dp)
                ) {
                    items(pendingOrders.size) { index ->
                        PendingOrderItem(order = pendingOrders[index], firebaseHelper = firebaseHelper) { orderId, status ->
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
                        text = "Danh sách của bạn",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp
                    )
                    TextButton(onClick = { navController.navigate(AllJobs.route) }) {
                        Text(text = "Tất cả")
                    }
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.2f)
//                        .height(100.dp)
                    ,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
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
fun PendingOrderItem(order: Order, firebaseHelper: FirebaseHelper, onAction: (String, String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("Loading...") }
    var serviceName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(order.userId) {
        coroutineScope.launch {
            userName = firebaseHelper.getUserNameById(order.userId)
            serviceName = firebaseHelper.getServiceNameById(order.serviceId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "Đơn hàng từ: $userName", style = MaterialTheme.typography.bodyLarge, fontSize = 16.sp)
        Text(text = "Dịch vụ: $serviceName", style = MaterialTheme.typography.bodyMedium, fontSize = 14.sp)
        Text(text = "Ngày: ${order.date}", style = MaterialTheme.typography.bodyMedium, fontSize = 14.sp)
        Text(text = "Địa chỉ: ${order.address}", style = MaterialTheme.typography.bodyMedium, fontSize = 14.sp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { onAction(order.id, "accepted") }) {
                Text("Chấp nhận")
            }
            Button(onClick = { onAction(order.id, "cancelled") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Từ chối")
            }
        }
    }
}
