package com.example.rentingproject.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.rentingproject.NavRoute.RentRouteController
import com.example.rentingproject.ui.theme.RentingProjectTheme
import com.example.rentingproject.utils.DataStoreHelper

//!!! xử lý với xml hay jetpack đây ta, chắc là theo jetpack đi, cho nó mới :D


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentingProjectTheme {
                // make scaffold for padding and adding top bar if needed

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RentRouteController(
                        modifier = Modifier.padding(innerPadding),
                        dataStoreHelper = DataStoreHelper(this)
                    )
                }
            }
        }
    }
}