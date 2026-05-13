package com.example.nammakathey.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DistrictBox(name: String, navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .background(Color.Transparent)
            .clickable {
                navController.navigate("district/$name")
            }
    )
}