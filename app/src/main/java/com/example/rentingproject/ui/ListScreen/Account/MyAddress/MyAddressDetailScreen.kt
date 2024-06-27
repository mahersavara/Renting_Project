package com.example.rentingproject.ui.ListScreen.Account.MyAddress

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.R
import com.example.rentingproject.database.model.address.Address
import com.example.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAddressDetailScreen(navController: NavController, addressId: String, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val coroutineScope = rememberCoroutineScope()
    var address by remember { mutableStateOf(Address()) }
    var isDefaultAddress by remember { mutableStateOf(false) }

    LaunchedEffect(addressId) {
        if (addressId != "new") {
            coroutineScope.launch {
                val fetchedAddress = firebaseHelper.getAddressById(addressId)
                if (fetchedAddress != null) {
                    address = fetchedAddress
                    isDefaultAddress = fetchedAddress.isDefault
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My address detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()
                    if (isDefaultAddress) {
                        val addresses = firebaseHelper.getUserAddresses(uid)
                        addresses.forEach { it.isDefault = false }
                        firebaseHelper.updateAddresses(addresses)
                    }
                    if (addressId == "new") {
                        firebaseHelper.addAddress(uid, address.copy(isDefault = isDefaultAddress)) { success ->
                            if (success) {
                                navController.popBackStack()
                            }
                        }
                    } else {
                        firebaseHelper.updateAddress(address.copy(isDefault = isDefaultAddress)) { success ->
                            if (success) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.ic_check), contentDescription = "Save Address")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = address.name,
                onValueChange = { address = address.copy(name = it) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address.phoneNumber,
                onValueChange = { address = address.copy(phoneNumber = it) },
                label = { Text("Phone number") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Text(text = "+84 |", modifier = Modifier.padding(end = 8.dp))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address.street,
                onValueChange = { address = address.copy(street = it) },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Default address", modifier = Modifier.weight(1f))
                Switch(checked = isDefaultAddress, onCheckedChange = { isDefaultAddress = it })
            }
        }
    }
}
