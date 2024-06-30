package com.example.rentingproject.ui.ListScreen.Account.LoginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.*
import com.example.rentingproject.R
import com.example.rentingproject.utils.FirebaseHelper

@Composable
fun LoginScreen(navController: NavController, modifier: Modifier = Modifier) {

    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailOrPhoneValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var rememberPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val firebaseHelper = FirebaseHelper()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Đăng nhập",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hãy đăng nhập để tiếp tục sử dụng app",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = emailOrPhone,
                onValueChange = {
                    emailOrPhone = it
                    isEmailOrPhoneValid = it.isNotEmpty()
                },
                label = { Text("Email/số điện thoại") },
                isError = !isEmailOrPhoneValid,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )
            if (!isEmailOrPhoneValid) {
                Text(text = "Email hoặc số điện thoại không hợp lệ", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isPasswordValid = it.isNotEmpty()
                },
                label = { Text("Mật khẩu") },
                isError = !isPasswordValid,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(painter = painterResource(id = if (passwordVisibility) R.drawable.ic_visibility_off else R.drawable.ic_visibility), contentDescription = null)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            if (!isPasswordValid) {
                Text(text = "Mật khẩu không hợp lệ", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberPassword,
                        onCheckedChange = { rememberPassword = it }
                    )
                    Text(text = "Ghi nhớ mật khẩu")
                }
                TextButton(onClick = { /* Handle Forgot Password */ }) {
                    Text(text = "Quên mật khẩu?")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (isEmailOrPhoneValid && isPasswordValid) {
                        firebaseHelper.auth.signInWithEmailAndPassword(emailOrPhone, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = firebaseHelper.auth.currentUser
                                    user?.let {
                                        firebaseHelper.getUserRole(it.uid) { role ->
                                            navController.navigate(if (role == "Cleaner") CleanerHome.route else HomeOwnerHome.route) {
                                                popUpTo(Login.route) { inclusive = true }
                                            }
                                        }
                                    }
                                } else {
                                    errorMessage = task.exception?.message ?: "Đăng nhập thất bại"
                                }
                            }
                    } else {
                        if (!isEmailOrPhoneValid) isEmailOrPhoneValid = false
                        if (!isPasswordValid) isPasswordValid = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Đăng nhập")
            }

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { /* Handle Login with phone number */ }) {
                Text(text = "Đăng nhập bằng số điện thoại")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { /* Handle Google Sign In */ }) {
                    Icon(painter = painterResource(id = R.drawable.ic_google), contentDescription = null, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = { /* Handle Facebook Sign In */ }) {
                    Icon(painter = painterResource(id = R.drawable.ic_facebook), contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate(SignUp.route) }) {
                Text(text = "Chưa có tài khoản? Đăng ký")
            }
        }
    }
}
