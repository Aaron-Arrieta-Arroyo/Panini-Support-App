package com.panini.supportapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.panini.supportapp.presentation.navigation.AppNavigation
import com.panini.supportapp.presentation.theme.SupportAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as SupportApp

        setContent {
            SupportAppTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    repository = app.container.ticketRepository
                )
            }
        }
    }
}
