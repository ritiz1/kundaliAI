// kotlin
package com.example.kundaliai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.kundaliai.navigation.AppNavGraph
import com.example.kundaliai.ui.theme.KundaliAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KundaliAITheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}
