package com.example.rentingproject.ui.ListScreen.Account.RegisterScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rentingproject.NavRoute.Login
import com.example.rentingproject.R
import com.example.rentingproject.utils.FirebaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPhoneNumberValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var isRepeatPasswordValid by remember { mutableStateOf(true) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val firebaseHelper = FirebaseHelper()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                    text = "Tạo tài khoản",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Vui lòng điền chi tiết để tạo tài khoản",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    },
                    label = { Text("Email") },
                    isError = !isEmailValid,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isEmailValid) {
                    Text(text = "Email không hợp lệ", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        isPhoneNumberValid = it.length >= 10
                    },
                    label = { Text("Số điện thoại") },
                    isError = !isPhoneNumberValid,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isPhoneNumberValid) {
                    Text(text = "Số điện thoại không hợp lệ", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        isPasswordValid = it.any { char -> char.isLetterOrDigit() }
                        isRepeatPasswordValid = it == repeatPassword
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
                    Text(text = "Mật khẩu phải có ít nhất một ký tự đặc biệt", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = repeatPassword,
                    onValueChange = {
                        repeatPassword = it
                        isRepeatPasswordValid = it == password
                    },
                    label = { Text("Nhập lại mật khẩu") },
                    isError = !isRepeatPasswordValid,
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(painter = painterResource(id = if (passwordVisibility) R.drawable.ic_visibility_off else R.drawable.ic_visibility), contentDescription = null)
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isRepeatPasswordValid) {
                    Text(text = "Mật khẩu không khớp", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = {
                        if (isEmailValid && isPhoneNumberValid && isPasswordValid && isRepeatPasswordValid) {
                            firebaseHelper.registerUser(email, password) { success, message ->
                                if (success) {
                                    val user = firebaseHelper.auth.currentUser
                                    user?.let {
                                        firebaseHelper.setUserRole(it.uid, "HomeOwner") { roleSuccess ->
                                            if (roleSuccess) {
                                                navController.navigate(Login.route) {
                                                    popUpTo(Login.route) { inclusive = true }
                                                }
                                            } else {
                                                errorMessage = "Không thể đặt vai trò người dùng"
                                            }
                                        }
                                    }
                                } else {
                                    errorMessage = message ?: "Đăng ký không thành công"
                                }
                            }
                        }
                    }) {
                        Text(text = "Đăng ký Chủ nhà")
                    }

                    Button(onClick = {
                        if (isEmailValid && isPhoneNumberValid && isPasswordValid && isRepeatPasswordValid) {
                            firebaseHelper.registerUser(email, password) { success, message ->
                                if (success) {
                                    val user = firebaseHelper.auth.currentUser
                                    user?.let {
                                        firebaseHelper.setUserRole(it.uid, "Cleaner") { roleSuccess ->
                                            if (roleSuccess) {
                                                navController.navigate(Login.route) {
                                                    popUpTo(Login.route) { inclusive = true }
                                                }
                                            } else {
                                                errorMessage = "Không thể đặt vai trò người dùng"
                                            }
                                        }
                                    }
                                } else {
                                    errorMessage = message ?: "Đăng ký không thành công"
                                }
                            }
                        }
                    }) {
                        Text(text = "Đăng ký Dọn dẹp")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { /* Handle Google Sign Up */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .graphicsLayer {
                                    alpha = 1f
                                },
                            alignment = Alignment.Center
                        )
                    }
                    IconButton(onClick = { /* Handle Facebook Sign Up */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_facebook),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .graphicsLayer {
                                    alpha = 1f
                                },
                            alignment = Alignment.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate(Login.route) }) {
                    Text(text = "Đã có tài khoản? Đăng nhập")
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
                }
            }
        }
    }
}
