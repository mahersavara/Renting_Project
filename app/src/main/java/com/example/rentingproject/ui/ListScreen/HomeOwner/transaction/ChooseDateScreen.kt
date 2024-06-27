package com.example.rentingproject.ui.ListScreen.HomeOwner.transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.DeliveryAddress
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChooseDateScreen(navController: NavController, serviceId: String) {
    var selectedDate by remember { mutableStateOf("") }
    val calendar = remember { Calendar.getInstance() }
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                showDatePicker = false
                showTimePicker = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedDate = SimpleDateFormat("EEE MMM dd yyyy, hh:mma", Locale.getDefault()).format(calendar.time)
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose Date", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            showDatePicker = true
        }) {
            Text("Pick Date and Time")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selected Date: $selectedDate")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedDate.isNotEmpty()) {
                    navController.navigate(DeliveryAddress.createRoute(serviceId, selectedDate))
                }
            }
        ) {
            Text("Next")
        }
    }
}
