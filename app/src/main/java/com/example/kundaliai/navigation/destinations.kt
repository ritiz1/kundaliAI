package com.example.kundaliai.navigation

interface Destination{
    val route:  String
}

object UserQueryDestination : Destination {
    override val route: String = "user_query"
}

object LiveSessionDestination : Destination {
    override val route: String = "live_session"
}

object HomeDestination : Destination{
    override val route: String="home"
}

object ProfileDestination : Destination {
    override val route :String = "profile"
}

object LiveSessionScreen: Destination {
    override val route: String = "livesessionscreen"
}

object ObBoardingScreen : Destination {
    override val route: String = "onBoarding"
}
