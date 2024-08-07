package eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import eriksu.commercial.rentingproject.NavRoute.ServiceDetail
import eriksu.commercial.rentingproject.R
import eriksu.commercial.rentingproject.model.job.Service
import eriksu.commercial.rentingproject.utils.FirebaseHelper
import kotlinx.coroutines.launch

@Composable
fun ServiceCard(navController: NavController, service: Service) {
    val firebaseHelper = FirebaseHelper()
    val uid = firebaseHelper.auth.currentUser?.uid.orEmpty()
    var isLiked by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Kiểm tra nếu dịch vụ đã được thích
    LaunchedEffect(Unit) {
        val likedServices = firebaseHelper.getLikedServices(uid)
        isLiked = likedServices.any { it.id == service.id }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate(ServiceDetail.createRoute(service.id)) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = service.images.firstOrNull()),
                contentDescription = "Hình ảnh dịch vụ",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = service.name, style = MaterialTheme.typography.bodyMedium)
            Text(text = service.location, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "\$${service.price}", style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Đánh giá",
                        tint = Color.Yellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(text = service.rating.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        if (isLiked) {
                            firebaseHelper.removeLikedService(uid, service.id)
                        } else {
                            firebaseHelper.addLikedService(uid, service)
                        }
                        isLiked = !isLiked
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    painter = painterResource(id = if (isLiked) R.drawable.ic_liked else R.drawable.ic_like),
                    contentDescription = "Nút thích",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
