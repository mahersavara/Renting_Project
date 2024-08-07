package eriksu.commercial.rentingproject.ui.ListScreen.Account.PersonalInfo

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import eriksu.commercial.rentingproject.R
import eriksu.commercial.rentingproject.utils.FirebaseHelper
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
    val genderOptions = listOf("Nam", "Nữ")

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileSize = context.contentResolver.openInputStream(it)?.available() ?: 0
            if (fileSize < 1024 * 1024) {
                profilePictureUri = it
                Toast.makeText(context, "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Kích thước ảnh phải nhỏ hơn 1MB", Toast.LENGTH_SHORT).show()
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
                        Timber.tag("PersonalInfoScreen").d("Không có tài liệu nào như vậy")
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.tag("PersonalInfoScreen").e(exception, "Lỗi khi lấy tài liệu")
                }
        }
    }

    val dialogState = rememberMaterialDialogState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin cá nhân") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (profilePictureUri != null) {
                Image(
                    painter = rememberImagePainter(profilePictureUri),
                    contentDescription = "Ảnh đại diện",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_me),
                    contentDescription = "Ảnh đại diện",
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
                label = { Text("Tên") },
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
                    label = { Text("Số điện thoại") },
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
                    label = { Text("Giới tính") },
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
                    label = { Text("Ngày sinh") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(painter = painterResource(id = R.drawable.ic_booking), contentDescription = "Chọn ngày")
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
                            Timber.tag("PersonalInfoScreen").d("Cập nhật hồ sơ người dùng thành công")
                            if (profilePictureUri != null) {
                                firebaseHelper.uploadProfilePicture(
                                    user.uid,
                                    profilePictureUri!!
                                ) { uri ->
                                    if (uri != null) {
                                        Toast.makeText(
                                            context,
                                            "Cập nhật hồ sơ thành công",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Không thể tải ảnh đại diện lên",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Cập nhật hồ sơ thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Timber.tag("PersonalInfoScreen")
                                .e(exception, "Không thể cập nhật hồ sơ")
                            Toast.makeText(context, "Không thể cập nhật hồ sơ", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Lưu")
            }
        }
    }

    MaterialDialog(dialogState = dialogState, buttons = {
        positiveButton("OK")
        negativeButton("Hủy")
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
            label = { Text("Mã quốc gia") },
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
