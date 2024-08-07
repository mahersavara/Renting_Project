package com.example.rentingproject.ui.ListScreen.HomeOwner.homescreen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.rentingproject.NavRoute.*
import com.example.rentingproject.R
import com.example.rentingproject.database.model.job.Service
import com.example.rentingproject.ui.components.BottomNavigationBar
import com.example.rentingproject.ui.components.ServiceCard
import com.example.rentingproject.utils.FirebaseHelper
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeOwnerHomepageScreen(navController: NavController, modifier: Modifier = Modifier) {
    val currentRoute = HomeOwnerHome.route
    val firebaseHelper = FirebaseHelper()
    var address by remember { mutableStateOf("") }
    var allServices by remember { mutableStateOf(listOf<Service>()) }
    var filteredServices by remember { mutableStateOf(listOf<Service>()) }
    var lastVisibleService by remember { mutableStateOf<DocumentSnapshot?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var searchJob: Job? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    val listState = rememberLazyListState()
    var userRole by remember { mutableStateOf<String?>(null) }

    fun loadMoreServices() {
        coroutineScope.launch {
            isLoading = true
            val newServicesWithSnapshots = firebaseHelper.getMoreServices(lastVisibleService)
            val newServices = newServicesWithSnapshots.map { it.first }
            if (newServices.isNotEmpty()) {
                allServices = allServices + newServices
                filteredServices = allServices
                lastVisibleService = newServicesWithSnapshots.last().second
            } else {
                Toast.makeText(context, "Đã tải hết các mục", Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }
    }

    fun fetchData() {
        coroutineScope.launch {
            val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()
            firebaseHelper.getUserRole(uid) { role ->
                userRole = role
            }
            val addresses = firebaseHelper.getUserAddresses(uid)
            val defaultAddress = addresses.find { it.isDefault }
            address = defaultAddress?.let { "${it.street}, ${it.city}, ${it.country}" } ?: "Chưa có địa chỉ mặc định"
            loadMoreServices()
        }
    }

    LaunchedEffect(Unit) {
        fetchData()
    }

    LaunchedEffect(searchQuery) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            delay(500)
            if (searchQuery.isEmpty()) {
                filteredServices = allServices
            } else {
                filteredServices = allServices.filter { service ->
                    service.name.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0
                lastVisibleItemIndex to listState.firstVisibleItemIndex
            }
            .distinctUntilChanged()
            .collect { (lastVisibleItemIndex, firstVisibleItemIndex) ->
                if (!isLoading && lastVisibleItemIndex >= filteredServices.size - 1 && firstVisibleItemIndex > 0) {
                    loadMoreServices()
                }
            }
    }

    Scaffold(
        bottomBar = {
            userRole?.let {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute, userRole = it)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Address Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Địa chỉ: $address", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { navController.navigate(MyAddress.route) }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Chỉnh sửa địa chỉ",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Booking Info
            Text(
                text = "Đặt lịch dọn dẹp cho nhà bạn tại: $address",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Tìm kiếm") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Tìm kiếm",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Services Section
            Text(
                text = "Các dịch vụ có sẵn",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (isLoading && allServices.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredServices.size) { index ->
                        ServiceCard(navController = navController, service = filteredServices[index])
                    }
                }
            }
        }
    }
}
