package com.example.rentingproject.ui.ListScreen.Account.Payment

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(navController: NavController, modifier: Modifier = Modifier) {
    var selectedMethod by remember { mutableStateOf("Tiền mặt") }

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
        },
        bottomBar = {
            Button(
                onClick = { navController.popBackStack() }, // Handle Confirm Action
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Xác nhận")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            PaymentMethodItem(
                icon = R.drawable.ic_cash,
                label = "Tiền mặt",
                selected = selectedMethod == "Tiền mặt",
                onSelect = { selectedMethod = "Tiền mặt" }
            )
        }
    }
}

@Composable
fun PaymentMethodItem(icon: Int, label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { onSelect() }
    ) {
        Icon(painter = painterResource(id = icon), contentDescription = label)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        RadioButton(selected = selected, onClick = onSelect)
    }
}
