package com.example.rentingproject.ui.ListScreen.IntroduceScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rentingproject.R
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import com.example.rentingproject.NavRoute.IntroduceScreen
import com.example.rentingproject.NavRoute.Login
import com.example.rentingproject.NavRoute.SignUp



@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@Composable
fun IntroduceScreen(navController: NavController, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> OnboardingPage(
                    imageRes = R.drawable.onboarding_1,
                    title = "Chào mừng đến với Home Clean!",
                    description = "ABC XYZ"
                )
                1 -> OnboardingPage(
                    imageRes = R.drawable.onboarding_2,
                    title = "Chăm sóc ngôi nhà của bạn",
                    description = "ABC XYZ"
                )
                2 -> OnboardingPage(
                    imageRes = R.drawable.onboarding_3,
                    title = "Đặt chỗ và giao tiếp",
                    description = "ABC XYZ",
                    showButtons = true,
                    onLoginClicked = {
                        navController.navigate(Login.route) {
                            popUpTo(IntroduceScreen.route) { inclusive = true }
                        }
                    },
                    onSignUpClicked = {
                        navController.navigate(SignUp.route) {
                            popUpTo(IntroduceScreen.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
                navController.navigate(Login.route) {
                    popUpTo(IntroduceScreen.route) { inclusive = true }
                }
            }) {
                Text("Bỏ qua")
            }

            Button(onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(pagerState.currentPage + 1)
                }
            }) {
                Text("Tiếp theo")
            }
        }
    }
}

@Composable
fun OnboardingPage(
    imageRes: Int,
    title: String,
    description: String,
    showButtons: Boolean = false,
    onLoginClicked: () -> Unit = {},
    onSignUpClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        )
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = description,
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        if (showButtons) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onLoginClicked) {
                Text("Đăng nhập")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSignUpClicked) {
                Text("Đăng ký")
            }
        }
    }
}