package com.example.kundaliai.navigation

interface Destination{
    val route:  String

}

object HomeDestination : Destination{
    override val route: String="home"
}

object ProfileDestination : Destination {
    override val route :String = "profile"
}

object VoiceChat: Destination {
    override val route: String = "voicechat"
}

object ObBoardingScreen : Destination {
    override val route: String = "onBoarding"
}