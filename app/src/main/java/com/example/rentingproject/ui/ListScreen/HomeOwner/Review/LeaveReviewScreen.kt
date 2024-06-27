package com.example.rentingproject.ui.ListScreen.Account.LeaveReview

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.TransactionHistory
import com.example.rentingproject.R
import com.example.rentingproject.utils.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveReviewScreen(navController: NavController, orderId: String, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf(TextFieldValue("")) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Leave a Review") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (isSubmitted) {
                    Text("Thank you for your review!", style = MaterialTheme.typography.bodyLarge)
                } else {
                    Text(text = "Rating", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingBar(rating = rating, onRatingChanged = { newRating -> rating = newRating })
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Write a review") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            isLoading = true
                            coroutineScope.launch {
                                firebaseHelper.leaveReview(currentUserId, orderId, rating, reviewText.text)
                                isLoading = false
                                isSubmitted = true
                                navController.navigate(TransactionHistory.route) {
                                    popUpTo(TransactionHistory.route) { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Review")
                    }
                }
            }
        }
    )
}

@Composable
fun RatingBar(rating: Int, onRatingChanged: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 1..5) {
            Icon(
                painter = painterResource(id = if (i <= rating) R.drawable.ic_star_filled else R.drawable.ic_star_outline),
                contentDescription = "Star $i",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingChanged(i) }
            )
        }
    }
}
