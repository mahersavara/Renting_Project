package com.example.rentingproject.NavRoute

class NavigationDestination {
}

interface Destination {
    val route: String
}


// Welcome Section
object SplashScreen : Destination {
    override val route = "splashscreen"
}

object IntroduceScreen: Destination{
    override val route = "introducescreen"
}

// Sprint 1: User Management System


object Account : Destination {
    override val route = "account"
}

object Login : Destination {
    override val route = "login"
}

object PersonalInfo : Destination {
    override val route = "personalinfo"
}

object MyAddress : Destination {
    override val route = "myaddress"
}
object MyAddressDetail: Destination{
    override val route = "myaddressdetail"
}

object SignUp : Destination {
    override val route = "signup"
}

object Message: Destination {
    override val route = "message"
}
object Inbox : Destination {
    const val userIdArg = "userId"
    override val route = "inbox/{$userIdArg}"
    // comments
    fun createRoute(userId: Int) = "inbox/$userId"
}

// Sprint 2: HomeOwner Management System

object HomeOwnerHome : Destination {
    override val route = "homeowner"
}


object ServiceDetail : Destination{
    override val route = "servicedetail"
}




        // Booking section
object BookingCalendar: Destination {
    override val route= "bookingcalendar"
}
// ??? tai sao hmmm chac la mo luc trong Booking section - cai nay khac voi TODO service detail yeppp
// chua co man hinh -> chua can lam, yep
object orderdetail:Destination{
    override val route= "orderdetail"
}

           // Liked Section
object Liked: Destination {
    override val route= "liked"
}
object servicedetail:Destination{
    override val route= "servicedetail"
}

object ratingdetail:Destination{
    override val route= "ratingdetail"
}

        // Book now section
object deliveryAddress:Destination{
    override val route= "deliveryAdress"
}
object deliveryAddressDetail:Destination{
    override val route= "deliveryAddressDetail"
}
object payment:Destination{
    override val route= "payment"
}
object orderstatus:Destination{
    override val route= "orderstatus"
}

            // Review section
object transactionhistory:Destination{
    override val route= "transactionhistory"
}
object transactionreview:Destination{
    override val route= "transactionreview"
}



//TODO: Sprint 3: Cleaner Management System UI

// yeah, chia nhu nay co ve hop ly day ^^

object CleanerHome : Destination {
    override val route = "cleaner"
}
    // my job section
object myjob : Destination {
    override val route = "myjob"
}

object myjobdetail : Destination {
    override val route = "myjobdetail"
}
object postnewjob : Destination {
    override val route = "postnewjob"
}

    // chung message screen
    // chung address book
    // chung payment option at profile







/*
// No Needed yet

// implement later
object LoginViaPhone : Destination {
    override val route = "loginviaphone"
}

object RegisterViaPhone : Destination {
    override val route = "registerviaphone"

}
object
*/