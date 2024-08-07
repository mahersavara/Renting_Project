// OrderSuccessScreen.kt
package com.example.rentingproject.ui.ListScreen.HomeOwner.transaction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eriksu.commercial.rentingproject.NavRoute.BookingCalendar
import eriksu.commercial.rentingproject.NavRoute.HomeOwnerHome
import eriksu.commercial.rentingproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSuccessScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Thành công") }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_success),
                    contentDescription = "Thành công",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Cảm ơn bạn", style = MaterialTheme.typography.headlineMedium)
                Text("Vì đã đặt dịch vụ", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { navController.navigate(BookingCalendar.route) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Xem trên lịch")
                    }

                    Button(
                        onClick = { navController.popBackStack(HomeOwnerHome.route, inclusive = false) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Về trang chủ")
                    }
                }
            }
        }
    )
}
