package com.example.rentingproject.ui.ListScreen.RegisterScreen
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
import com.example.rentingproject.NavRoute.Login
import com.example.rentingproject.R

@Composable
fun SignUpScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPhoneNumberValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var passwordVisibility by remember { mutableStateOf(false) }

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
                text = "Create account",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please fill the details to create account",
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
                Text(text = "Invalid email", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    isPhoneNumberValid = it.length >= 10
                },
                label = { Text("Phone number") },
                isError = !isPhoneNumberValid,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            if (!isPhoneNumberValid) {
                Text(text = "Invalid phone number", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isPasswordValid = it.any { char -> char.isLetterOrDigit() }
                },
                label = { Text("Password") },
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
                Text(text = "Password must have at least one special character", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { /* Handle Homeowner Sign Up */
                // get to HomeOwnerScreen
                }) {
                    Text(text = "Homeowner Sign Up")
                }

                Button(onClick = { /* Handle Cleaner Sign Up */
                    // get to Handle Cleaner Screen
                }) {
                    Text(text = "Cleaner Sign Up")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { /* Handle Google Sign Up */
                //TODO need analyze for an alert dialog chosing
                }) {
                    Icon(painter = painterResource(id = R.drawable.ic_google), contentDescription = null, modifier = Modifier.size(40.dp))
                }
                IconButton(onClick = { /* Handle Facebook Sign Up */

                    //TODO need analyze for an alert dialog chosing
                }) {

                    Icon(painter = painterResource(id = R.drawable.ic_facebook), contentDescription = null, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate(Login.route) }) {
                Text(text = "Already have an account? Login")
            }
        }
    }
}
