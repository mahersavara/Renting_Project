package com.example.rentingproject.ui.ListScreen.Account.Personalnfo

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.rentingproject.R
import com.example.rentingproject.utils.FirebaseHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import timber.log.Timber
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(navController: NavController, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneCountryCode by remember { mutableStateOf("+84") }
    var phoneNumber by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    val genderOptions = listOf("Male", "Female")

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileSize = context.contentResolver.openInputStream(it)?.available() ?: 0
            if (fileSize < 1024 * 1024) {
                profilePictureUri = it
                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Image size should be less than 1MB", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    LaunchedEffect(firebaseHelper.auth.currentUser) {
        firebaseHelper.auth.currentUser?.let { user ->
            val userDocRef = Firebase.firestore.collection("users").document(user.uid)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        name = document.getString("name") ?: ""
                        email = document.getString("email") ?: ""
                        phoneNumber = document.getString("phoneNumber") ?: ""
                        gender = document.getString("gender") ?: ""
                        birthday = document.getString("birthday") ?: ""
                        profilePictureUri =
                            document.getString("profilePicture")?.let { Uri.parse(it) }
                    } else {
                        Timber.tag("PersonalInfoScreen").d("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.tag("PersonalInfoScreen").e(exception, "get failed with ")
                }
        }
    }

    val dialogState = rememberMaterialDialogState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = { Text("Personal Info") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (profilePictureUri != null) {
                Image(
                    painter = rememberImagePainter(profilePictureUri),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_me),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { },
                label = { Text("Email") },
                readOnly = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                CountryCodePicker(selectedCountryCode = phoneCountryCode) { selectedCode ->
                    phoneCountryCode = selectedCode
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    genderOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                gender = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .clickable { dialogState.show() }
            ) {
                OutlinedTextField(
                    value = birthday,
                    onValueChange = { },
                    label = { Text("Birthday") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(painter = painterResource(id = R.drawable.ic_booking), contentDescription = "Select Date")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                firebaseHelper.auth.currentUser?.let { user ->
                    val userDocRef = Firebase.firestore.collection("users").document(user.uid)
                    val updates = mapOf(
                        "name" to name,
                        "email" to email,
                        "phoneNumber" to phoneCountryCode + phoneNumber,
                        "gender" to gender,
                        "birthday" to birthday
                    )
                    userDocRef.update(updates)
                        .addOnSuccessListener {
                            Timber.tag("PersonalInfoScreen").d("User profile updated")
                            if (profilePictureUri != null) {
                                firebaseHelper.uploadProfilePicture(
                                    user.uid,
                                    profilePictureUri!!
                                ) { uri ->
                                    if (uri != null) {
                                        Toast.makeText(
                                            context,
                                            "Profile updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to upload profile picture",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Timber.tag("PersonalInfoScreen")
                                .e(exception, "Failed to update profile")
                            Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save")
            }
        }
    }

    MaterialDialog(dialogState = dialogState, buttons = {
        positiveButton("OK")
        negativeButton("Cancel")
    }) {
        datepicker { date ->
            birthday = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodePicker(selectedCountryCode: String, onCountryCodeSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val countryCodes = listOf("+84", "+1", "+44", "+91", "+61", "+81") // Example country codes

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCountryCode,
            onValueChange = { },
            label = { Text("Country Code") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .width(100.dp)
                .clickable { expanded = !expanded }
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countryCodes.forEach { code ->
                DropdownMenuItem(
                    text = { Text(code) },
                    onClick = {
                        onCountryCodeSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}
