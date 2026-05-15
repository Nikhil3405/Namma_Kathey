package com.example.nammakathey.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.nammakathey.data.local.loadHeroes
import com.example.nammakathey.ui.screens.HomeScreen
import com.example.nammakathey.ui.screens.DistrictScreen
import com.example.nammakathey.ui.screens.HeroListScreen
import com.example.nammakathey.ui.screens.ProfileScreen
import com.example.nammakathey.ui.screens.QuizScreen
import com.example.nammakathey.ui.screens.StoryScreen
import com.example.nammakathey.viewmodel.AppViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun MainScreen(parentNavController: NavHostController) {

    val navController = rememberNavController()
    val appViewModel: AppViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        "home",
        "district",
        "profile"
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController)
            }
        }
    ){ padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {

            composable("home") {
                HomeScreen(
                    navController = navController,
                    appViewModel = appViewModel
                )
            }

            composable("district") {

                val context = LocalContext.current

                val completedHeroes by appViewModel.completedHeroes.collectAsState()

                val allData = remember {
                    loadHeroes(context)
                }

                // 🔥 Compute completed districts
                val completedDistricts = allData.districts.filter { district ->
                    district.heroes.isNotEmpty() && district.heroes.all { hero ->
                        completedHeroes.contains(hero.id)
                    }
                }.map { it.name_en }

                DistrictScreen(
                    completedDistricts = completedDistricts,
                    totalDistricts     = allData.districts.size,
                    onDistrictClick = { districtName ->
                        val encoded = java.net.URLEncoder.encode(districtName, "UTF-8")
                        navController.navigate("hero_list/$encoded")
                    },
                )
            }

            composable("hero_list/{districtName}") { backStackEntry ->

                val encodedName = backStackEntry.arguments?.getString("districtName") ?: ""

                val districtName = URLDecoder.decode(
                    encodedName,
                    StandardCharsets.UTF_8.toString()
                )
                HeroListScreen(districtName = districtName ?: "", navController,appViewModel)
            }

            composable("story/{heroId}/{districtName}") { backStackEntry ->

                val heroId = backStackEntry.arguments?.getString("heroId") ?: ""
                val encodedName = backStackEntry.arguments?.getString("districtName") ?: ""

                val districtName = URLDecoder.decode(
                    encodedName,
                    StandardCharsets.UTF_8.toString()
                )
                StoryScreen(heroId = heroId, districtName = districtName,navController)
            }

            composable("quiz/{heroId}/{districtName}") { backStackEntry ->

                val heroId = backStackEntry.arguments?.getString("heroId") ?: ""
                val encodedName = backStackEntry.arguments?.getString("districtName") ?: ""

                val districtName = URLDecoder.decode(
                    encodedName,
                    StandardCharsets.UTF_8.toString()
                )
                QuizScreen(
                    heroId = heroId,
                    districtName = java.net.URLDecoder.decode(districtName, "UTF-8"),
                    appViewModel = appViewModel,
                    navController = navController   // 👈 ADD THIS
                )
            }

            composable("profile") {
                ProfileScreen(appViewModel=appViewModel)
            }
        }

    }

}

