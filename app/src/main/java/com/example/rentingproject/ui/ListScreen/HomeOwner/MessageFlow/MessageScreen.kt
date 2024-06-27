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
import coil.compose.rememberAsyncImagePainter
import com.example.rentingproject.NavRoute.Inbox
import com.example.rentingproject.NavRoute.Message
import com.example.rentingproject.R
import com.example.rentingproject.ui.components.BottomNavigationBar
import com.example.rentingproject.utils.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(navController: NavController, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var conversations by remember { mutableStateOf(listOf<Conversation>()) }

    LaunchedEffect(Unit) {
        firebaseHelper.getConversationsForUser(currentUserId) { fetchedConversations ->
            conversations = fetchedConversations
        }
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
                title = { Text("Messages") },
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
                items(conversations.size) { index ->
                    val item = conversations[index]
                    ConversationItemView(item, navController, firebaseHelper)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            BottomNavigationBar(navController = navController, currentRoute = Message.route, userRole = "HomeOwner")
        }
    }
}

@Composable
fun ConversationItemView(item: Conversation, navController: NavController, firebaseHelper: FirebaseHelper) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate(Inbox.createRoute(item.id,item.participants))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = item.avatar),
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
            Text(item.timestamp.toString(), style = MaterialTheme.typography.bodySmall)
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


data class Conversation(
    val id: String = "",
    val name: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val avatar: String = "",
    val participants: List<String> = listOf() // Add participants list
)