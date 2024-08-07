package eriksu.commercial.rentingproject.NavRoute

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.MessageFlow.MessageScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Account.AccountScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Account.LeaveReview.LeaveReviewScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Account.MyAddress.MyAddressDetailScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Account.MyAddress.MyAddressScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Account.Payment.PaymentMethodScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Cleaner.jobs.AllJobsScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Cleaner.jobs.EditJobScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Cleaner.jobs.PostJobScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Cleaner.myjob.MyJobScreen
import eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.ServiceJob.LikedServiceScreen
import eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.transaction.ChooseDateScreen
import eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.transaction.DeliveryAddressScreen
import eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.transaction.OrderSuccessScreen
import eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.transaction.PaymentScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Account.LoginScreen.LoginScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Account.PersonalInfo.PersonalInfoScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Account.RegisterScreen.SignUpScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Account.TransactionHistory.TransactionHistoryScreen
import eriksu.commercial.rentingproject.ui.ListScreen.Cleaner.homescreen.CleanerHomePage
import eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.Booking.BookingScreen
import eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.MessageFlow.InboxScreen
import eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.ServiceJob.ServiceDetailScreen
import eriksu.commercial.rentingproject.ui.ListScreen.HomeOwner.homescreen.HomeOwnerHomepageScreen
import eriksu.commercial.rentingproject.ui.ListScreen.IntroduceScreen.IntroduceScreen
import eriksu.commercial.rentingproject.ui.ListScreen.SplashScreen.SplashScreen
import eriksu.commercial.rentingproject.utils.DataStoreHelper
import eriksu.commercial.rentingproject.utils.FirebaseHelper


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RentRouteController(modifier: Modifier = Modifier, dataStoreHelper: DataStoreHelper) {
    val navController = rememberNavController()
    val firebaseHelper = FirebaseHelper()
    var userRole by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(firebaseHelper.auth.currentUser) {
        firebaseHelper.auth.currentUser?.let { user ->
            firebaseHelper.getUserRole(user.uid) { role ->
                userRole = role
                navController.navigate(if (role == "Cleaner") CleanerHome.route else HomeOwnerHome.route) {
                    popUpTo(SplashScreen.route) { inclusive = true }
                }
            }
        } ?: run {
            navController.navigate(SplashScreen.route)
        }
    }


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
            arguments = listOf(
                navArgument(Inbox.conversationIdArg) { type = NavType.StringType },
                navArgument(Inbox.participantsArg) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString(Inbox.conversationIdArg) ?: ""
            val participantsStr = backStackEntry.arguments?.getString(Inbox.participantsArg) ?: ""
            val participants = participantsStr.split(",")

            InboxScreen(navController = navController, conversationId = conversationId, participants = participants)
        }


        composable(MyAddress.route) { MyAddressScreen(navController = navController) }
        composable(
            route = "${MyAddressDetail.route}/{addressId}",
            arguments = listOf(navArgument("addressId") { type = NavType.StringType })
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId") ?: "new"
            MyAddressDetailScreen(navController = navController, addressId = addressId)
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
            // maybe go to the delivery route -> oder success / fail
        }

        // ! Same name ( package/class ) with keyword leading to fail the code leading to java.lang.NoClassDefFoundError: Failed resolution of:
        composable(
            route = ServiceDetail.route,
            arguments = listOf(navArgument(ServiceDetail.serviceIdArg) { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString(ServiceDetail.serviceIdArg) ?: ""
            ServiceDetailScreen(navController, serviceId)
        }
        composable(Liked.route){
            LikedServiceScreen(navController)
        }

        composable(TransactionHistory.route) {
            TransactionHistoryScreen(navController = navController)
        }
        composable(
            route = LeaveReview.route,
            arguments = listOf(navArgument(LeaveReview.orderIdArg) { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(LeaveReview.orderIdArg) ?: ""
            LeaveReviewScreen(navController = navController, orderId = orderId)
        }


        //CLeaner section
        composable(CleanerHome.route) { CleanerHomePage(navController = navController) }
            //? for request section, custommer implement wrong at this stage :(
        composable(MyJob.route) { MyJobScreen(navController = navController) }
        composable(AllJobs.route) { AllJobsScreen(navController = navController) }
        composable(PostJob.route) { PostJobScreen(navController = navController) }
        composable(
            route = EditJob.route,
            arguments = listOf(navArgument(EditJob.serviceIdArg) { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString(EditJob.serviceIdArg) ?: ""
            EditJobScreen(navController = navController, serviceId = serviceId)
        }


        //TODO book section
        composable(
            route = ChooseDate.route,
            arguments = listOf(navArgument(ChooseDate.serviceIdArg) { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString(ChooseDate.serviceIdArg) ?: ""
            ChooseDateScreen(navController = navController, serviceId = serviceId)
        }


        composable(
            route = DeliveryAddress.route,
            arguments = listOf(
                navArgument(DeliveryAddress.serviceIdArg) { type = NavType.StringType },
                navArgument(DeliveryAddress.dateArg) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString(DeliveryAddress.serviceIdArg) ?: ""
            val date = backStackEntry.arguments?.getString(DeliveryAddress.dateArg) ?: ""
            DeliveryAddressScreen(navController = navController, serviceId = serviceId, date = date)
        }

        composable(
            route = PaymentBooking.route,
            arguments = listOf(
                navArgument(PaymentBooking.serviceIdArg) { type = NavType.StringType },
                navArgument(PaymentBooking.dateArg) { type = NavType.StringType },
                navArgument(PaymentBooking.addressArg) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString(PaymentBooking.serviceIdArg) ?: ""
            val date = backStackEntry.arguments?.getString(PaymentBooking.dateArg) ?: ""
            val address = backStackEntry.arguments?.getString(PaymentBooking.addressArg) ?: ""
            PaymentScreen(navController = navController, serviceId = serviceId, date = date, address = address)
        }


        composable(
            route = OrderSuccess.route
        ) {
            OrderSuccessScreen(navController = navController)
        }


    }


}