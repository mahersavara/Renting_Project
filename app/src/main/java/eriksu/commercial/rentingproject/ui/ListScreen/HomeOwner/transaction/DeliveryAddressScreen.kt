package com.example.rentingproject.ui.ListScreen.HomeOwner.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eriksu.commercial.rentingproject.utils.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import eriksu.commercial.rentingproject.NavRoute.MyAddressDetail
import eriksu.commercial.rentingproject.NavRoute.PaymentBooking
import eriksu.commercial.rentingproject.R
import eriksu.commercial.rentingproject.model.address.Address
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryAddressScreen(navController: NavController, serviceId: String, date: String, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var addresses by remember { mutableStateOf(listOf<Address>()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            addresses = firebaseHelper.getUserAddresses(currentUserId)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chọn địa chỉ") },
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(addresses) { address ->
                    AddressItem(address = address, onClick = {
                        navController.navigate(PaymentBooking.createRoute(serviceId, date, address.street))
                    })
                }
            }
        }
    }
}

@Composable
fun AddressItem(address: Address, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = address.street,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${address.city}, ${address.country}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}
