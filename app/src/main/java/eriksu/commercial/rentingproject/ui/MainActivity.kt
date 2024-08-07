package eriksu.commercial.rentingproject.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import eriksu.commercial.rentingproject.NavRoute.RentRouteController
import eriksu.commercial.rentingproject.ui.theme.RentingProjectTheme
import eriksu.commercial.rentingproject.utils.DataStoreHelper

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentingProjectTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.systemBars
                ) { innerPadding ->
                    RentRouteController(
                        modifier = Modifier.padding(innerPadding),
                        dataStoreHelper = DataStoreHelper(this)
                    )
                }
            }
        }
    }
}
