package com.example.rentingproject.ui.ListScreen.Account.MyAddress

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.MyAddressDetail
import com.example.rentingproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAddressScreen(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        // sau cho cai nay ra thanh component rieng
        topBar = {
            TopAppBar(
                title = { Text(text = "My address") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(MyAddressDetail.route) }) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = "Add new address")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(4) { index ->
                AddressItem(navController)
            }
        }
    }
}

@Composable
fun AddressItem(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { navController.navigate(MyAddressDetail.route) }
    ) {
        Text(text = "38 Ton Duc Thang Avenue", style = MaterialTheme.typography.titleMedium)
        Text(text = "38 Ton Duc Thang Avenue, Vietnam", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Nghia Khuat | 1234 5678", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_default), contentDescription = "Default Address", tint = Color.Green)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Default", color = Color.Green, fontSize = 12.sp)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { navController.navigate(MyAddressDetail.route) }) {
                Icon(painter = painterResource(id = R.drawable.ic_edit), contentDescription = "Edit Address")
            }
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
    }
}
