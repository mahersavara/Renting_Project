package com.example.rentingproject.ui.ListScreen.HomeOwner.MessageFlow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.R
import com.example.rentingproject.database.model.message.Message
import com.example.rentingproject.utils.FirebaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(navController: NavController, userId: Int, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        messages = firebaseHelper.getMessagesForConversation(userId)
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
                title = { Text("Conversation") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                items(messages.size) { index ->
                    val message = messages[index]
                    MessageItemView(message)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = { Text("Say something") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            firebaseHelper.sendMessage(userId, messageText)
                            messageText = ""
                        }
                    )
                )

                IconButton(onClick = {
                    firebaseHelper.sendMessage(userId, messageText)
                    messageText = ""
                }) {
                    Icon(Icons.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun MessageItemView(message: Message) {
    val isOwnMessage = message.sender == "Me"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        if (!isOwnMessage) {
            Image(
                painter = painterResource(id = message.avatar),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .background(
                    if (isOwnMessage) Color.LightGray else MaterialTheme.colorScheme.primary,
                    MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            Text(message.sender, style = MaterialTheme.typography.bodyMedium, color = if (isOwnMessage) Color.Black else Color.White)
            Text(message.content, style = MaterialTheme.typography.bodyLarge, color = if (isOwnMessage) Color.Black else Color.White)
        }
    }
}
