package eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.MessageFlow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil.compose.rememberAsyncImagePainter
import eriksu.commercial.rentingproject.utils.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eriksu.commercial.rentingproject.R
import eriksu.commercial.rentingproject.model.message.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(navController: NavController, conversationId: String, participants: List<String>, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var messageText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var isSent by remember { mutableStateOf(true) }

    // Listen for real-time updates
    LaunchedEffect(isSent) {
        firebaseHelper.listenForMessages(conversationId) { fetchedMessages ->
            messages = fetchedMessages
        }
        isSent = false
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(6000)
            firebaseHelper.listenForMessages(conversationId) { fetchedMessages ->
                messages = fetchedMessages
            }
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
                title = { Text("Cuộc trò chuyện") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Quay lại")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages) { message ->
                    MessageItemView(message, currentUserId)
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
                    label = { Text("Nói gì đó") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (messageText.isNotBlank()) {
                                coroutineScope.launch {
                                    firebaseHelper.sendMessage(conversationId, messageText, participants)
                                    messageText = ""
                                    isSent = true
                                }
                            }
                        }
                    )
                )

                IconButton(onClick = {
                    if (messageText.isNotBlank()) {
                        coroutineScope.launch {
                            firebaseHelper.sendMessage(conversationId, messageText, participants)
                            messageText = ""
                        }
                    }
                }) {
                    Icon(Icons.Filled.Send, contentDescription = "Gửi")
                }
            }
        }
    }
}

@Composable
fun MessageItemView(message: Message, currentUserId: String) {
    val isOwnMessage = message.sender == currentUserId
    var avatarUrl by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("Người dùng ẩn danh") }

    LaunchedEffect(message.sender) {
        val userDoc = FirebaseFirestore.getInstance().collection("users").document(message.sender).get().await()
        avatarUrl = userDoc.getString("profilePicture").orEmpty()
        userName = userDoc.getString("name").orEmpty().ifBlank { "Người dùng ẩn danh" }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        if (!isOwnMessage) {
            Image(
                painter = rememberAsyncImagePainter(model = avatarUrl),
                contentDescription = "Ảnh đại diện",
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
            Text(userName, style = MaterialTheme.typography.bodyMedium, color = if (isOwnMessage) Color.Black else Color.White)
            Text(message.content, style = MaterialTheme.typography.bodyLarge, color = if (isOwnMessage) Color.Black else Color.White)
            val date = java.util.Date(message.timestamp)
            val format = java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
            Text(format.format(date), style = MaterialTheme.typography.bodySmall, color = if (isOwnMessage) Color.Black else Color.White)
        }
    }
}
