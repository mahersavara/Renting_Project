// PaymentScreen.kt
package com.example.rentingproject.ui.ListScreen.HomeOwner.transaction

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.OrderSuccess
import com.example.rentingproject.R
import com.example.rentingproject.utils.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController, serviceId: String, date: String, address: String, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var paymentMethod by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Phương thức thanh toán") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Chọn phương thức thanh toán", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

//            Button(onClick = { paymentMethod = "Thẻ tín dụng" }) {
//                Text("Thẻ tín dụng")
//            }

//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(onClick = { paymentMethod = "PayPal" }) {
//                Text("PayPal")
//            }
            Button(onClick = { paymentMethod = "Tiền mặt" }) {
                Text("Tiền mặt")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (paymentMethod.isNotEmpty()) {
                        coroutineScope.launch {
                            navController.navigate(OrderSuccess.route)
                            firebaseHelper.saveOrder(currentUserId, serviceId, date, address, paymentMethod)
                        }
                    }
                }
            ) {
                Text("Xác nhận và thanh toán")
            }
        }
    }
}
