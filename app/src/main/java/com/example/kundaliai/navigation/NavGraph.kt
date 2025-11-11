package com.example.kundaliai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kundaliai.LiveSessionScreen
import com.example.kundaliai.LiveSessionScreene
import com.example.kundaliai.UserQueryForm
import com.example.kundaliai.ui.viewmodels.UserQueryViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = UserQueryDestination.route
    ) {
        composable(UserQueryDestination.route) {
            val viewModel: UserQueryViewModel = viewModel()
            val submitSuccess by viewModel.submitSuccess.collectAsState()

            UserQueryForm(
                onSubmit = { birthData ->
                    viewModel.submitBirthData(birthData)
                }
            )

            // Navigate when submission is successful
            LaunchedEffect(submitSuccess) {
                if (submitSuccess) {
                    // Create username from name for navigation
                    val username = viewModel.getLastSubmittedName()
                    navController.navigate("${LiveSessionDestination.route}/$username") {
                        popUpTo(UserQueryDestination.route) { inclusive = false }
                    }
                    viewModel.resetSubmitSuccess()
                }
            }
        }

        composable(
            route = "${LiveSessionDestination.route}/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            if (username.isNotEmpty()) {
                LiveSessionScreen(username = username)
            } else {
                LiveSessionScreene()
            }
        }

        // Fallback route without username
        composable(LiveSessionDestination.route) {
            LiveSessionScreene()
        }
    }
}
