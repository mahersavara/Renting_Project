package eriksu.commercial.rentingproject.ui.ListScreen.Cleaner.jobs

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
import eriksu.commercial.rentingproject.R
import eriksu.commercial.rentingproject.model.job.Service
import eriksu.commercial.rentingproject.utils.FirebaseHelper
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
                Timber.tag("EditJobScreen").e(exception, "Lỗi khi lấy dịch vụ")
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
            Toast.makeText(context, "Một số hình ảnh không được chọn vì vượt quá 1MB", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Hình ảnh đã được chọn", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chỉnh sửa (${service.name})") },
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
                value = service.name,
                onValueChange = { service = service.copy(name = it) },
                label = { Text("Tên dịch vụ") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = service.price,
                onValueChange = { service = service.copy(price = it) },
                label = { Text("Giá cả") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = service.location,
                onValueChange = { service = service.copy(location = it) },
                label = { Text("Địa điểm") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = service.description,
                onValueChange = { service = service.copy(description = it) },
                label = { Text("Mô tả") },
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
                                Toast.makeText(context, "Xóa dịch vụ thành công", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Xóa dịch vụ thất bại", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Xóa")
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
                                                    Toast.makeText(context, "Cập nhật dịch vụ thành công", Toast.LENGTH_SHORT).show()
                                                    navController.popBackStack()
                                                } else {
                                                    Toast.makeText(context, "Cập nhật dịch vụ thất bại", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Tải lên hình ảnh thất bại", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            firebaseHelper.editService(service) { success ->
                                if (success) {
                                    Toast.makeText(context, "Cập nhật dịch vụ thành công", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Cập nhật dịch vụ thất bại", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                ) {
                    Text(text = "Lưu")
                }
            }
        }
    }
}
