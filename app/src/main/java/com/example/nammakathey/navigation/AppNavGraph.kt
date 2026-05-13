package com.example.nammakathey.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.nammakathey.ui.screens.HomeScreen
import com.example.nammakathey.ui.screens.DistrictScreen
import com.example.nammakathey.ui.screens.ProfileScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main" // 🔥 important
    ) {
        composable("main") {
            MainScreen(navController)
        }
    }
}