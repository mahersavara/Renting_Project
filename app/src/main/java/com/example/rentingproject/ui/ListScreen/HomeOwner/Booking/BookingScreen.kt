package com.example.rentingproject.ui.ListScreen.HomeOwner.Booking

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.*
import com.example.rentingproject.R
import com.example.rentingproject.database.model.Order
import com.example.rentingproject.ui.components.BottomNavItem
import com.example.rentingproject.ui.components.BottomNavigationBar
import com.example.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(navController: NavController, modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    val firebaseHelper = FirebaseHelper()
    var bookings by remember { mutableStateOf(listOf<Order>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()
            bookings = firebaseHelper.getAcceptedOrders(uid)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Booking") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = "booking")
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.background)
        ) {
            CalendarView(selectedDate = selectedDate, onDateSelected = { date ->
                selectedDate = date
            }, bookings = bookings)
            Spacer(modifier = Modifier.height(16.dp))
            BookingList(selectedDate = selectedDate, bookings = bookings, onCancel = { orderId ->
                coroutineScope.launch {
                    firebaseHelper.updateOrderStatus(orderId, "cancelled") { success ->
                        if (success) {
                            coroutineScope.launch {   bookings = firebaseHelper.getAcceptedOrders(firebaseHelper.auth.currentUser?.uid.orEmpty()) }
                        }
                    }
                }
            })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit, bookings: List<Order>) {
    val currentMonth = YearMonth.now()
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onDateSelected(selectedDate.minusMonths(1)) }) {
                Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Previous Month")
            }
            Text(
                text = "${currentMonth.month.name.lowercase().capitalize(Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onDateSelected(selectedDate.plusMonths(1)) }) {
                Icon(painter = painterResource(id = R.drawable.ic_forward), contentDescription = "Next Month")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            items(daysInMonth + firstDayOfMonth) { index ->
                val day = index - firstDayOfMonth + 1
                if (index >= firstDayOfMonth) {
                    val date = LocalDate.of(currentMonth.year, currentMonth.month, day)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onDateSelected(date) }
                    ) {
                        Text(
                            text = date.dayOfWeek.name.take(3),
                            fontSize = 12.sp,
                            fontWeight = if (selectedDate == date) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = day.toString(),
                            fontSize = 18.sp,
                            fontWeight = if (selectedDate == date) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedDate == date) MaterialTheme.colorScheme.primary else Color.Unspecified
                        )
                        if (bookings.any { LocalDate.parse(it.date, DateTimeFormatter.ofPattern("EEE MMM dd yyyy, hh:mma", Locale.getDefault())) == date }) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingList(selectedDate: LocalDate, bookings: List<Order>, onCancel: (String) -> Unit) {
    val filteredBookings = bookings.filter { LocalDate.parse(it.date, DateTimeFormatter.ofPattern("EEE MMM dd yyyy, hh:mma", Locale.getDefault())) == selectedDate }

    if (filteredBookings.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No plan has been made yet")
        }
    } else {
        LazyColumn {
            items(filteredBookings) { booking ->
                BookingItem(booking, onCancel)
            }
        }
    }
}

@Composable
fun BookingItem(booking: Order, onCancel: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "${booking.date} - ${booking.serviceId}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pickup Location: ${booking.address}",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle Chat */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_message), contentDescription = "Chat")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onCancel(booking.id) }) {
                Icon(painter = painterResource(id = R.drawable.ic_cancel), contentDescription = "Cancel")
            }
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
    }
}

suspend fun FirebaseHelper.getAcceptedOrders(uid: String): List<Order> {
    Timber.d("Fetching services for cleaner with uid: $uid")
    return try {
        // Fetch services provided by the cleaner
        val servicesSnapshot = db.collection("services")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        val serviceIds = servicesSnapshot.documents.mapNotNull { it.id }
        Timber.d("Fetched ${serviceIds.size} services for cleaner with uid: $uid")

        // Fetch orders for those services
        val ordersSnapshot = db.collection("orders")
            .whereIn("serviceId", serviceIds)
            .whereEqualTo("status", "accepted")
            .get()
            .await()

        val orders = ordersSnapshot.documents.mapNotNull { it.toObject(Order::class.java) }

        Timber.d("Fetched ${orders.size} accepted orders for cleaner with uid: $uid")
        orders.forEach { order ->
            Timber.d("Order ID: ${order.id}, Service ID: ${order.serviceId}, Date: ${order.date}, Status: ${order.status}")
        }

        orders
    } catch (e: Exception) {
        Timber.e(e, "Error fetching accepted orders for cleaner with uid: $uid")
        emptyList()
    }
}
