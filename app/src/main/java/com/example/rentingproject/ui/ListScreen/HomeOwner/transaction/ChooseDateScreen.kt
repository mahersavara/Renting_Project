package com.example.rentingproject.ui.ListScreen.HomeOwner.transaction

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Button
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.DeliveryAddress
import com.example.rentingproject.R
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseDateScreen(navController: NavController, serviceId: String) {
    var selectedDateVietnamese by remember { mutableStateOf("") }
    var selectedDateEnglish by remember { mutableStateOf("") }
    val calendar = remember { Calendar.getInstance() }
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                showDatePicker = false
                showTimePicker = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.setOnShowListener {
            val positiveButton: Button = datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            positiveButton.text = "Chọn"
            val negativeButton: Button = datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            negativeButton.text = "Hủy"
        }

        datePickerDialog.show()
    }

    if (showTimePicker) {
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val dateFormatVietnamese = SimpleDateFormat("EEE MMM dd yyyy, hh:mma", Locale.getDefault())
                val dateFormatEnglish = SimpleDateFormat("EEE MMM dd yyyy, hh:mma", Locale.ENGLISH)
                selectedDateVietnamese = dateFormatVietnamese.format(calendar.time)
                selectedDateEnglish = dateFormatEnglish.format(calendar.time)
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )

        timePickerDialog.setOnShowListener {
            val positiveButton: Button = timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
            positiveButton.text = "Chọn"
            val negativeButton: Button = timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)
            negativeButton.text = "Hủy"
        }

        timePickerDialog.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chọn ngày") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Chọn ngày và giờ", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    showDatePicker = true
                }) {
                    Text("Chọn ngày và giờ")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedDateVietnamese.isNotEmpty()) {
                    Text("Ngày đã chọn: $selectedDateVietnamese", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedDateEnglish.isNotEmpty()) {
                            navController.navigate(DeliveryAddress.createRoute(serviceId, selectedDateEnglish))
                        }
                    },
                    enabled = selectedDateEnglish.isNotEmpty()
                ) {
                    Text("Tiếp theo")
                }
            }
        }
    )
}
