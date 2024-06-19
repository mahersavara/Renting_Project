package com.example.rentingproject.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.rentingproject.R

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String, modifier: Modifier = Modifier) {
    val items = listOf(
        //TODO checking for homeowner or cleaner
        BottomNavItem("homeowner", R.drawable.ic_home, "Home"),
        BottomNavItem("liked", R.drawable.ic_liked, "Liked"),
        BottomNavItem("booking", R.drawable.ic_booking, "Booking"),
        BottomNavItem("message", R.drawable.ic_message, "Message"),
        BottomNavItem("account", R.drawable.ic_me, "Account")
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
                            // Avoid multiple copies of the same destination in the back stack
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                            // Clear the back stack to the root of the graph
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
