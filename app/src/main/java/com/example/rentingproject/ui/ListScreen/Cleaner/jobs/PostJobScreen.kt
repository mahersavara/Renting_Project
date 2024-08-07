package com.example.rentingproject.ui.ListScreen.Cleaner.jobs

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(navController: NavController, modifier: Modifier = Modifier) {
    var serviceName by remember { mutableStateOf("") }
    var priceRange by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val firebaseHelper = FirebaseHelper()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val validUris = uris.filter { uri ->
            val fileSize = context.contentResolver.openInputStream(uri)?.available() ?: 0
            fileSize < 1024 * 1024
        }
        imageUris = validUris
        if (validUris.size < uris.size) {
            Toast.makeText(context, "Một số hình ảnh không được chọn vì vượt quá 1MB", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Hình ảnh đã được chọn", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Đăng dịch vụ mới") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    Image(painter = painterResource(id = R.drawable.ic_camera), contentDescription = "Tải lên hình ảnh", modifier = Modifier.size(50.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = serviceName,
                onValueChange = { serviceName = it },
                label = { Text("Tên dịch vụ") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = priceRange,
                onValueChange = { priceRange = it },
                label = { Text("Khoảng giá") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Địa điểm") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (imageUris.isNotEmpty()) {
                        val uploadedImageUrls = mutableListOf<String>()
                        imageUris.forEach { uri ->
                            firebaseHelper.uploadServiceImage(serviceName, uri) { imageUrl ->
                                if (imageUrl != null) {
                                    uploadedImageUrls.add(imageUrl)
                                    if (uploadedImageUrls.size == imageUris.size) {
                                        val service = Service(
                                            name = serviceName,
                                            price = priceRange,
                                            location = location,
                                            description = description,
                                            images = uploadedImageUrls
                                        )
                                        firebaseHelper.postService(service) { success ->
                                            if (success) {
                                                Toast.makeText(context, "Đăng dịch vụ thành công", Toast.LENGTH_SHORT).show()
                                                navController.popBackStack()
                                            } else {
                                                Toast.makeText(context, "Đăng dịch vụ thất bại", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Tải lên hình ảnh thất bại", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Vui lòng chọn ít nhất một hình ảnh", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Lưu")
            }
        }
    }
}
