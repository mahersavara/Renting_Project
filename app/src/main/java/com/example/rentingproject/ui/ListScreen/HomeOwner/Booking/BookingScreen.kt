package com.example.rentingproject.ui.ListScreen.HomeOwner.Booking

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.Account
import com.example.rentingproject.NavRoute.BookingCalendar
import com.example.rentingproject.NavRoute.HomeOwnerHome
import com.example.rentingproject.NavRoute.Liked
import com.example.rentingproject.NavRoute.Message
import com.example.rentingproject.R
import com.example.rentingproject.ui.components.BottomNavItem
import com.example.rentingproject.ui.components.BottomNavigationBar
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(navController: NavController, modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    val bookings = remember { mutableStateOf(sampleBookings()) }

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
            }, bookings = bookings.value)
            Spacer(modifier = Modifier.height(16.dp))
            BookingList(selectedDate = selectedDate, bookings = bookings.value)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit, bookings: List<Booking>) {
    // Mock Calendar View
    // Replace with a real calendar component
    val dates = (1..30).map { LocalDate.of(selectedDate.year, selectedDate.month, it) }

    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        items(dates) { date ->
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
                    text = date.dayOfMonth.toString(),
                    fontSize = 18.sp,
                    fontWeight = if (selectedDate == date) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedDate == date) MaterialTheme.colorScheme.primary else Color.Unspecified
                )
                if (bookings.any { it.date == date }) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun BookingList(selectedDate: LocalDate, bookings: List<Booking>) {
    val filteredBookings = bookings.filter { it.date == selectedDate }

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
                BookingItem(booking)
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "${booking.time} - ${booking.service}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pickup Location: ${booking.location}",
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
                Icon(painter = painterResource(id = R.drawable.ic_cancel), contentDescription = "Cancel")
            }
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
    }
}

data class Booking(val date: LocalDate, val time: String, val service: String, val location: String)

@RequiresApi(Build.VERSION_CODES.O)
fun sampleBookings(): List<Booking> {
    val today = LocalDate.now()
    return listOf(
        Booking(today, "10:00-11:00", "Cleaning with Cleaner A", "38 Ton Duc Thang"),
        Booking(today, "14:00-15:00", "Training with Trainer B", "38 Ton Duc Thang"),
        Booking(today.plusDays(1), "19:00-20:00", "Play with A", "38 Ton Duc Thang")
    )
}

data class BottomNavItem(val route: String, val icon: Int, val label: String)

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String, modifier: Modifier = Modifier) {
    val items = listOf(
        BottomNavItem(HomeOwnerHome.route, R.drawable.ic_home, "Home"),
        BottomNavItem(Liked.route, R.drawable.ic_liked_bottom, "Liked"),
        BottomNavItem(BookingCalendar.route, R.drawable.ic_booking, "Booking"),
        BottomNavItem(Message.route, R.drawable.ic_message, "Message"),
        BottomNavItem(Account.route, R.drawable.ic_me, "Account")
    )

    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.label) },
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
