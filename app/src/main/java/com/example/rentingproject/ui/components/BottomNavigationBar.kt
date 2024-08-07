package com.example.rentingproject.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.Account
import com.example.rentingproject.NavRoute.BookingCalendar
import com.example.rentingproject.NavRoute.CleanerHome
import com.example.rentingproject.NavRoute.HomeOwnerHome
import com.example.rentingproject.NavRoute.Liked
import com.example.rentingproject.NavRoute.Message
import com.example.rentingproject.NavRoute.MyJob
import com.example.rentingproject.R

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String, userRole: String = "HomeOwner", @SuppressLint("ModifierParameter") modifier: Modifier = Modifier) {
    val items = if (userRole == "HomeOwner") {
        listOf(
            BottomNavItem(HomeOwnerHome.route, R.drawable.ic_home, "Trang chủ"),
            BottomNavItem(Liked.route, R.drawable.ic_liked_bottom, "Đã thích"),
            BottomNavItem(BookingCalendar.route, R.drawable.ic_booking, "Đặt chỗ"),
//            BottomNavItem(Message.route, R.drawable.ic_message, "Tin nhắn"),
            BottomNavItem(Account.route, R.drawable.ic_me, "Tài khoản")
        )
    } else {
        listOf(
            BottomNavItem(CleanerHome.route, R.drawable.ic_home, "Trang chủ"),
            BottomNavItem(MyJob.route, R.drawable.ic_job, "Công việc của tôi"),
            BottomNavItem(BookingCalendar.route, R.drawable.ic_booking, "Đặt chỗ"),
//            BottomNavItem(Message.route, R.drawable.ic_message, "Tin nhắn"),
            BottomNavItem(Account.route, R.drawable.ic_me, "Tài khoản")
        )
    }

    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp) // Adjust the size as needed
                    )
                },
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

data class BottomNavItem(val route: String, val icon: Int, val label: String)
