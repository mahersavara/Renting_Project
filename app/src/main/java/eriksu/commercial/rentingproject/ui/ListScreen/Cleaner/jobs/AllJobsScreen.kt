package eriksu.commercial.rentingproject.ui.ListScreen.Cleaner.jobs

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eriksu.commercial.rentingproject.NavRoute.AllJobs
import eriksu.commercial.rentingproject.NavRoute.PostJob
import eriksu.commercial.rentingproject.R
import eriksu.commercial.rentingproject.model.job.Service
import eriksu.commercial.rentingproject.ui.components.BottomNavigationBar
import eriksu.commercial.rentingproject.ui.components.JobItem
import eriksu.commercial.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllJobsScreen(navController: NavController, modifier: Modifier = Modifier) {
    val firebaseHelper = FirebaseHelper()
    val coroutineScope = rememberCoroutineScope()
    var services by remember { mutableStateOf(listOf<Service>()) }
    var isLoading by remember { mutableStateOf(true) }
    val userRole = "Cleaner"

    fun fetchData() {
        coroutineScope.launch {
            val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()
            services = firebaseHelper.getServices(uid)
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchData()
    }

    // Add this to refetch data when coming back to the screen
    navController.addOnDestinationChangedListener { _, destination, _ ->
        if (destination.route == AllJobs.route) {
            fetchData()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Danh sách của bạn") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = AllJobs.route, userRole = userRole)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(PostJob.route) }) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = "Thêm")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(services.size) { index ->
                        JobItem(navController = navController, job = services[index])
                    }
                }

//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp)
//                ) {
//                    items(services.size) { index ->
//                        JobItem(navController = navController, job = services[index])
//                    }
//                }
            }
        }
    }
}
