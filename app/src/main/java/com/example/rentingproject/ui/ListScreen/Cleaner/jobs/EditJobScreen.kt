package com.example.rentingproject.ui.ListScreen.Cleaner.jobs

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentingproject.R
import com.example.rentingproject.database.model.job.Service
import com.example.rentingproject.utils.FirebaseHelper
import timber.log.Timber

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJobScreen(navController: NavController, serviceId: String, modifier: Modifier = Modifier) {
    var service by remember { mutableStateOf(Service()) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val firebaseHelper = FirebaseHelper()
    val context = LocalContext.current

    LaunchedEffect(serviceId) {
        firebaseHelper.db.collection("services").document(serviceId).get()
            .addOnSuccessListener { document ->
                document.toObject(Service::class.java)?.let {
                    service = it
                }
            }
            .addOnFailureListener { exception ->
                Timber.tag("EditJobScreen").e(exception, "Error fetching service")
            }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val validUris = uris.filter { uri ->
            val fileSize = context.contentResolver.openInputStream(uri)?.available() ?: 0
            fileSize < 1024 * 1024
        }
        imageUris = validUris
        if (validUris.size < uris.size) {
            Toast.makeText(context, "Some images were not selected because they exceed 1MB", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Images selected", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit (${service.name})") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Icon(painter = painterResource(id = R.drawable.ic_camera), contentDescription = "Upload Image")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = service.name,
                onValueChange = { service = service.copy(name = it) },
                label = { Text("Service Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = service.price,
                onValueChange = { service = service.copy(price = it) },
                label = { Text("Price Range") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = service.location,
                onValueChange = { service = service.copy(location = it) },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = service.description,
                onValueChange = { service = service.copy(description = it) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        firebaseHelper.deleteService(service.id) { success ->
                            if (success) {
                                Toast.makeText(context, "Service deleted successfully", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Failed to delete service", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Delete")
                }
                Button(
                    onClick = {
                        if (imageUris.isNotEmpty()) {
                            val uploadedImageUrls = mutableListOf<String>()
                            imageUris.forEach { uri ->
                                firebaseHelper.uploadServiceImage(service.id, uri) { imageUrl ->
                                    if (imageUrl != null) {
                                        uploadedImageUrls.add(imageUrl)
                                        if (uploadedImageUrls.size == imageUris.size) {
                                            service = service.copy(images = service.images + uploadedImageUrls)
                                            firebaseHelper.editService(service) { success ->
                                                if (success) {
                                                    Toast.makeText(context, "Service updated successfully", Toast.LENGTH_SHORT).show()
                                                    navController.popBackStack()
                                                } else {
                                                    Toast.makeText(context, "Failed to update service", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            firebaseHelper.editService(service) { success ->
                                if (success) {
                                    Toast.makeText(context, "Service updated successfully", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Failed to update service", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}
