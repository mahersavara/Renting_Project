package eriksu.commercial.rentingproject.ui.ListScreen.Account

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import eriksu.commercial.rentingproject.NavRoute.*
import eriksu.commercial.rentingproject.R
import eriksu.commercial.rentingproject.ui.components.BottomNavigationBar
import eriksu.commercial.rentingproject.utils.FirebaseHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController, modifier: Modifier = Modifier) {
    val currentRoute = Account.route
    val firebaseHelper = FirebaseHelper()
    var userRole by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf("Chỉnh sửa tên mặc định") }
    var userPhoneNumber by remember { mutableStateOf("+84900989278") }
    var userProfilePicture by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    LaunchedEffect(firebaseHelper.auth.currentUser) {
        firebaseHelper.auth.currentUser?.let { user ->
            firebaseHelper.getUserRole(user.uid) { role ->
                Timber.tag("AccountScreen").d("User Role: %s", role)
                userRole = role
            }
            val userDocRef = Firebase.firestore.collection("users").document(user.uid)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userName = document.getString("name") ?: "Chỉnh sửa tên mặc định"
                        userPhoneNumber = document.getString("phoneNumber") ?: "+84900989278"
                        userProfilePicture = document.getString("profilePicture")?.let { Uri.parse(it) }
                    } else {
                        Timber.tag("AccountScreen").d("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.tag("AccountScreen").e(exception, "get failed with ")
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tài khoản") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            userRole?.let { BottomNavigationBar(navController, currentRoute, it) }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (userProfilePicture != null) {
                        Image(
                            painter = rememberImagePainter(userProfilePicture),
                            contentDescription = "Hình đại diện",
                            modifier = Modifier.size(64.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_me),
                            contentDescription = "Hình đại diện",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(userPhoneNumber, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Chỉnh sửa hồ sơ",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.navigate(PersonalInfo.route)
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3E0))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Xác minh email của bạn để bảo vệ tài khoản tốt hơn.", modifier = Modifier.weight(1f))
                        Button(onClick = { /* Xử lý xác minh email */ }) {
                            Text("Xác minh")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (userRole == "HomeOwner") {
                    ListItem("Lịch sử giao dịch", id = R.drawable.ic_trans_history, navController = navController, route = TransactionHistory.route)
                }

                ListItem("Quyền riêng tư & Bảo mật", id = R.drawable.ic_privacy, navController = navController)
                ListItem("Phương thức thanh toán", id = R.drawable.ic_payment_method, navController = navController, route = payment.route)
                ListItem("Địa chỉ", id = R.drawable.ic_address, navController = navController, route = MyAddress.route)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        firebaseHelper.auth.signOut()
                        navController.navigate(Login.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Đăng xuất")
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ListItem(text: String, @DrawableRes id: Int, navController: NavController, route: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                route?.let {
                    navController.navigate(it)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Image(painter = painterResource(id = id), contentDescription = null, modifier = Modifier.size(24.dp))
    }
}
