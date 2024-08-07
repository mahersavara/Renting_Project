package com.example.rentingproject.ui.ListScreen.SplashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.IntroduceScreen
import com.example.rentingproject.NavRoute.Login
import com.example.rentingproject.NavRoute.SplashScreen
import com.example.rentingproject.R
import com.example.rentingproject.utils.DataStoreHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun SplashScreen(navController: NavController, dataStoreHelper: DataStoreHelper) {
    val scope = rememberCoroutineScope()
    var isFirstTime by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        scope.launch {
            dataStoreHelper.isFirstTime.collect { firstTime ->
                isFirstTime = firstTime
            }
        }
        delay(3000)
        if (isFirstTime) {
            navController.navigate(IntroduceScreen.route) {
                popUpTo(SplashScreen.route) { inclusive = true }
            }
            dataStoreHelper.setFirstTime(false)
        } else {
            navController.navigate(Login.route) {
                popUpTo(SplashScreen.route) { inclusive = true }
            }
        }
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val logoSize = (screenWidth * 0.4f).coerceAtMost(150.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Splash",
            modifier = Modifier.size(logoSize)
        )
    }
}
