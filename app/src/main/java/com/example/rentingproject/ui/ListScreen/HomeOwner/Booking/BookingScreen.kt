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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.*
import com.example.rentingproject.R
import com.example.rentingproject.database.model.Order
import com.example.rentingproject.ui.components.BottomNavigationBar
import com.example.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(navController: NavController, modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val firebaseHelper = FirebaseHelper()
    var bookings by remember { mutableStateOf(listOf<Order>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()
            firebaseHelper.getUserRole(uid) { role ->
                coroutineScope.launch {
                    bookings = firebaseHelper.getAcceptedOrders(uid, role == "HomeOwner")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Đặt chỗ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Quay lại")
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
            MonthlyCalendarView(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                },
                onMonthChanged = { month ->
                    currentMonth = month
                },
                bookings = bookings
            )
            Spacer(modifier = Modifier.height(16.dp))
            BookingList(selectedDate = selectedDate, bookings = bookings)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyCalendarView(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    bookings: List<Order>
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value
    val vietnameseMonths = listOf(
        "Tháng Một", "Tháng Hai", "Tháng Ba", "Tháng Tư", "Tháng Năm", "Tháng Sáu",
        "Tháng Bảy", "Tháng Tám", "Tháng Chín", "Tháng Mười", "Tháng Mười Một", "Tháng Mười Hai"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
                Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Tháng trước")
            }
            Text(
                text = "${vietnameseMonths[currentMonth.month.value - 1]} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
                Icon(painter = painterResource(id = R.drawable.ic_forward), contentDescription = "Tháng sau")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                }
            }
            for (week in 0 until ((daysInMonth + firstDayOfMonth - 1) / 7 + 1)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    for (day in 1..7) {
                        val dateIndex = week * 7 + day - firstDayOfMonth + 1
                        if (dateIndex in 1..daysInMonth) {
                            val date = LocalDate.of(currentMonth.year, currentMonth.month, dateIndex)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .clickable { onDateSelected(date) }
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontSize = 18.sp,
                                    fontWeight = if (selectedDate == date) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedDate == date) MaterialTheme.colorScheme.primary else Color.Unspecified
                                )
                                if (bookings.any { LocalDate.parse(it.date, DateTimeFormatter.ofPattern("EEE MMM dd yyyy, hh:mma")) == date }) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingList(selectedDate: LocalDate, bookings: List<Order>) {
    val filteredBookings = bookings.filter {
        LocalDate.parse(it.date, DateTimeFormatter.ofPattern("EEE MMM dd yyyy, hh:mma")) == selectedDate
    }

    if (filteredBookings.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Chưa có kế hoạch nào được lên lịch")
        }
    } else {
        LazyColumn {
            items(filteredBookings) { booking ->
                BookingItem(booking)
            }
        }
    }
}

@Composable
fun BookingItem(booking: Order) {
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
            text = "Địa điểm đón: ${booking.address}",
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
            IconButton(onClick = { /* Handle Cancel */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_cancel), contentDescription = "Hủy")
            }
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
    }
}

suspend fun FirebaseHelper.getAcceptedOrders(uid: String, isHomeOwner: Boolean): List<Order> {
    Timber.d("Fetching accepted orders for user with uid: $uid")
    return try {
        val ordersSnapshot = if (isHomeOwner) {
            db.collection("orders")
                .whereEqualTo("userId", uid)
                .whereEqualTo("status", "accepted")
                .get()
                .await()
        } else {
            // Fetch services provided by the cleaner
            val servicesSnapshot = db.collection("services")
                .whereEqualTo("userId", uid)
                .get()
                .await()

            val serviceIds = servicesSnapshot.documents.mapNotNull { it.id }
            Timber.d("Fetched ${serviceIds.size} services for cleaner with uid: $uid")

            // Fetch orders for those services
            db.collection("orders")
                .whereIn("serviceId", serviceIds)
                .whereEqualTo("status", "accepted")
                .get()
                .await()
        }

        val orders = ordersSnapshot.documents.mapNotNull { it.toObject(Order::class.java) }

        Timber.d("Fetched ${orders.size} accepted orders for user with uid: $uid")
        orders.forEach { order ->
            Timber.d("Order ID: ${order.id}, Service ID: ${order.serviceId}, Date: ${order.date}, Status: ${order.status}")
        }

        orders
    } catch (e: Exception) {
        Timber.e(e, "Error fetching accepted orders for user with uid: $uid")
        emptyList()
    }
}
