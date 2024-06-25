package com.example.rentingproject.ui.ListScreen.HomeOwner.MessageFlow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.Inbox
import com.example.rentingproject.NavRoute.Message
import com.example.rentingproject.R
import com.example.rentingproject.database.model.message.InboxItem
import com.example.rentingproject.ui.components.BottomNavigationBar
import com.example.rentingproject.utils.FirebaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(navController: NavController, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    var inboxItems by remember { mutableStateOf(listOf<InboxItem>()) }

    LaunchedEffect(Unit) {
        inboxItems = firebaseHelper.getInboxItems(firebaseHelper.auth.currentUser?.uid.orEmpty())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = { Text("Message") },
                actions = {
                    IconButton(onClick = { /* Handle delete action */ }) {
                        Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(inboxItems.size) { index ->
                    val item = inboxItems[index]
                    InboxItemView(item, navController, firebaseHelper)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            BottomNavigationBar(navController = navController, currentRoute = Message.route, userRole = "HomeOwner") // Assume "HomeOwner" as default role for bottom navigation
        }
    }
}

@Composable
fun InboxItemView(item: InboxItem, navController: NavController, firebaseHelper: FirebaseHelper) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Mark message as read
                if (!item.isRead) {
                    firebaseHelper.markMessageAsRead(item.id)
                }
                // Navigate to InboxScreen
                navController.navigate(Inbox.createRoute(item.id))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.avatar),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(item.name, style = MaterialTheme.typography.bodyLarge)
            Text(item.lastMessage, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(item.timestamp, style = MaterialTheme.typography.bodySmall)
            if (!item.isRead) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color.Blue, CircleShape)
                )
            }
        }
    }
}
