package com.example.rentingproject.ui.ListScreen.SplashScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.IntroduceScreen
import com.example.rentingproject.NavRoute.Login
import com.example.rentingproject.NavRoute.SplashScreen
import com.example.rentingproject.R
import com.example.rentingproject.utils.DataStoreHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


@Composable
fun SplashScreen(navController: NavController, dataStoreHelper: DataStoreHelper) {
    val scope = rememberCoroutineScope()
    var isFirstTime by remember { mutableStateOf(true) }
// TODO SETTING IF CONDITION HERE, IF FIRST TIME THEN GO TO SOME INTRODUCE SCREEN ELSE GO TO LOGIN SCREEN
    LaunchedEffect(key1 = true) {
        //fix bug
        Log.d("AAAAA", "SplashScreen: 0")
        scope.launch {
            dataStoreHelper.isFirstTime.collect { firstTime ->
                isFirstTime = firstTime
            }
        }
        delay(3000) // Delay for 3 seconds
        Log.d("AAAAA", "SplashScreen: 0.5")
        if (isFirstTime) {
            navController.navigate(IntroduceScreen.route) {
                //TODO CHECK BUGS OF THIS ONE
                Log.d("AAAAA", "SplashScreen: 1")
//                popUpTo(SplashScreen.route) { inclusive = true }
            }
            dataStoreHelper.setFirstTime(false)
        } else {
            Log.d("AAAAA", "SplashScreen: 2")
            // Later for checking in the firebase if having firebase
            // Coding your own project, mixing with internet, not clone from A sample Project in Internet
            navController.navigate(Login.route) {
                popUpTo(SplashScreen.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Splash Logo",
            modifier = Modifier
                .wrapContentSize()
                .scale(3f)
        )
    }
}