package eriksu.commercial.rentingproject.ui.ListScreen.Account.MyAddress

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eriksu.commercial.rentingproject.R
import eriksu.commercial.rentingproject.model.address.Address
import eriksu.commercial.rentingproject.utils.FirebaseHelper
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
                title = { Text(text = "Chi tiết địa chỉ của tôi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Quay lại")
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
                Icon(painter = painterResource(id = R.drawable.ic_check), contentDescription = "Lưu địa chỉ")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = address.name,
                onValueChange = { address = address.copy(name = it) },
                label = { Text("Tên") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address.phoneNumber,
                onValueChange = { address = address.copy(phoneNumber = it) },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Text(text = "+84 |", modifier = Modifier.padding(end = 8.dp))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address.street,
                onValueChange = { address = address.copy(street = it) },
                label = { Text("Địa chỉ") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Địa chỉ mặc định", modifier = Modifier.weight(1f))
                Switch(
                    checked = isDefaultAddress,
                    onCheckedChange = { isDefaultAddress = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}