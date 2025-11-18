package com.example.kundaliai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kundaliai.HomeScreen
import com.example.kundaliai.LiveSessionScreen
import com.example.kundaliai.Prefs
import com.example.kundaliai.UserQueryForm
import com.example.kundaliai.ui.viewmodels.UserQueryViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context= LocalContext.current
    //read once on composition to decide start destination
    val onboardingDone = remember { Prefs.isOnBoardingDone(context)}


    NavHost(
        navController = navController,
        startDestination = if(onboardingDone) HomeDestination.route else UserQueryDestination.route
    ) {
        composable(UserQueryDestination.route) {
            val viewModel: UserQueryViewModel = viewModel()
            val submitSuccess by viewModel.submitSuccess.collectAsState()
            val userId by viewModel.generatedUserId.collectAsState()
            UserQueryForm(
                onSubmit = { birthData ->
                    viewModel.submitBirthData(birthData)
                }
            )

            //when submission succeed: Mark onboarding done only if it wasn't done earlier.
            // otherwise just return to prev screen ( e.g Home) after saving edit
            LaunchedEffect(submitSuccess,userId) {
                if (submitSuccess && userId !=null) {
                    // Create username from name for navigation


                    if(!onboardingDone){
                        //first time onboarding complete: persist flag and navigate to livesessionscreen.
                        //removing onboarding from back stack.
                        Prefs.setOnboardingDone(context, true)
                        navController.navigate("${LiveSessionDestination.route}/$userId") {
                            popUpTo(UserQueryDestination.route) { inclusive = true }
                        }
                    }
                    else{
                        //opened for editing from Home: Navigate back to previous screen(HOME)
                        navController.popBackStack()
                    }

                    viewModel.resetSubmitSuccess()
                }
            }
        }

        composable(
            route = "${LiveSessionDestination.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""

            LiveSessionScreen(username = userId)

        }

        composable(HomeDestination.route){
            HomeScreen(navController = navController)
        }
    }
}
