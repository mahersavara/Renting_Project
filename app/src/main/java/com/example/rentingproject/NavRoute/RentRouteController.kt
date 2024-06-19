package com.example.rentingproject.NavRoute

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rentingproject.ui.ListScreen.Account.AccountScreen
import com.example.rentingproject.ui.ListScreen.Account.MyAddress.MyAddressDetailScreen
import com.example.rentingproject.ui.ListScreen.Account.MyAddress.MyAddressScreen
import com.example.rentingproject.ui.ListScreen.Account.Payment.PaymentMethodScreen
import com.example.rentingproject.ui.ListScreen.Account.Personalnfo.PersonalInfoScreen
import com.example.rentingproject.ui.ListScreen.Cleaner.CleanerScreen
import com.example.rentingproject.ui.ListScreen.Cleaner.homescreen.CleanerHomePage
import com.example.rentingproject.ui.ListScreen.Cleaner.jobs.AllJobsScreen
import com.example.rentingproject.ui.ListScreen.Cleaner.jobs.EditJobScreen
import com.example.rentingproject.ui.ListScreen.Cleaner.jobs.PostJobScreen
import com.example.rentingproject.ui.ListScreen.Cleaner.myjob.MyJobScreen
import com.example.rentingproject.ui.ListScreen.HomeOwner.Booking.BookingScreen
import com.example.rentingproject.ui.ListScreen.HomeOwner.HomeOwnerHomepageScreen
import com.example.rentingproject.ui.ListScreen.HomeOwner.MessageFlow.InboxScreen
import com.example.rentingproject.ui.ListScreen.HomeOwner.MessageFlow.MessageScreen
import com.example.rentingproject.ui.ListScreen.HomeOwner.Review.LeaveReviewScreen
import com.example.rentingproject.ui.ListScreen.HomeOwner.Review.TransactionHistoryScreen
import com.example.rentingproject.ui.ListScreen.HomeOwner.ServiceJob.ServiceDetailScreen
import com.example.rentingproject.ui.ListScreen.IntroduceScreen.IntroduceScreen
import com.example.rentingproject.ui.ListScreen.LoginScreen.LoginScreen
import com.example.rentingproject.ui.ListScreen.RegisterScreen.SignUpScreen
import com.example.rentingproject.ui.ListScreen.SplashScreen.SplashScreen
import com.example.rentingproject.utils.DataStoreHelper


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RentRouteController(modifier: Modifier = Modifier, dataStoreHelper: DataStoreHelper) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = SplashScreen.route) {
        composable(SplashScreen.route) { SplashScreen(navController, dataStoreHelper) }
        composable(IntroduceScreen.route) {
            IntroduceScreen(
                navController = navController
            )
        }
        // general
        composable(Login.route) { LoginScreen(navController = navController) }
        composable(SignUp.route) { SignUpScreen(navController = navController) }
        composable(Account.route) { AccountScreen(navController = navController) }
        composable(PersonalInfo.route) { PersonalInfoScreen(navController = navController) }
        composable(
            route = Inbox.route,
            arguments = listOf(navArgument(Inbox.userIdArg) { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(Inbox.userIdArg) ?: 0
            InboxScreen(navController = navController, userId = userId)
        }

        composable(MyAddress.route) {
            MyAddressScreen(navController = navController)
        }
        composable(MyAddressDetail.route) {
            MyAddressDetailScreen(navController)
        }
        // change payment method
        composable(payment.route) {
            PaymentMethodScreen(navController)
        }

        composable(Message.route) { MessageScreen(navController = navController) }


        //Homeowner section
        composable(HomeOwnerHome.route) { HomeOwnerHomepageScreen(navController = navController) }

        // Booking section
        composable(BookingCalendar.route) {
            BookingScreen(navController)
        }

        // ! Same name ( package/class ) with keyword leading to fail the code leading to java.lang.NoClassDefFoundError: Failed resolution of:
        composable(ServiceDetail.route) {
            ServiceDetailScreen(navController = navController)
        }

        composable("transaction_history") {
            TransactionHistoryScreen(navController = navController)
        }
        composable("leave_review/{serviceName}") { backStackEntry ->
            val serviceName = backStackEntry.arguments?.getString("serviceName") ?: ""
            LeaveReviewScreen(navController = navController, serviceName = serviceName)
        }

        //CLeaner section
        composable(CleanerHome.route) { CleanerHomePage(navController = navController) }
            //? for request section, custommer implement wrong at this stage :(
        composable(myjob.route) {
            MyJobScreen(navController = navController)
        }
        composable("all_jobs") {
            AllJobsScreen(navController = navController)
        }
        composable("post_job") {
            PostJobScreen(navController = navController)
        }
        composable("edit_job/{serviceName}") { backStackEntry ->
            val serviceName = backStackEntry.arguments?.getString("serviceName") ?: ""
            EditJobScreen(navController = navController, serviceName = serviceName)
        }

    }


}