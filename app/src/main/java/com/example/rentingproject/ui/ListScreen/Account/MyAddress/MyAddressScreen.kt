package com.example.rentingproject.ui.ListScreen.Account.MyAddress

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.MyAddressDetail
import com.example.rentingproject.R
import com.example.rentingproject.database.model.address.Address
import com.example.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAddressScreen(navController: NavController, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    var addresses by remember { mutableStateOf(listOf<Address>()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            addresses = firebaseHelper.getUserAddresses(firebaseHelper.auth.currentUser?.uid.orEmpty())
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Địa chỉ của tôi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Quay lại")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("${MyAddressDetail.route}/new") }) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = "Thêm địa chỉ mới")
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(addresses.size) { index ->
                    AddressItem(navController, addresses[index]) {
                        coroutineScope.launch {
                            addresses = firebaseHelper.getUserAddresses(firebaseHelper.auth.currentUser?.uid.orEmpty())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddressItem(navController: NavController, address: Address, onDelete: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val firebaseHelper = FirebaseHelper()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("${MyAddressDetail.route}/${address.id}") }
    ) {
        Text(text = address.street, style = MaterialTheme.typography.titleMedium)
        Text(text = "${address.street}, ${address.city}, ${address.country}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "${address.name} | ${address.phoneNumber}", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (address.isDefault) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_default),
                    contentDescription = "Địa chỉ mặc định",
                    tint = Color.Green,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Mặc định", color = Color.Green, fontSize = 12.sp)
            } else {
                IconButton(onClick = {
                    coroutineScope.launch {
                        firebaseHelper.deleteAddress(address.id) { success ->
                            if (success) {
                                onDelete()
                            }
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Xóa địa chỉ",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { navController.navigate("${MyAddressDetail.route}/${address.id}") }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Chỉnh sửa địa chỉ",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
    }
}
