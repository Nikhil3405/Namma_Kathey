package com.example.nammakathey.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LanguageToggle(isEnglish: Boolean, onToggle: (Boolean) -> Unit) {

    Row {
        Button(
            onClick = { onToggle(true) },
            colors = if (isEnglish) ButtonDefaults.buttonColors()
            else ButtonDefaults.outlinedButtonColors()
        ) {
            Text("English")
        }

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            onClick = { onToggle(false) },
            colors = if (!isEnglish) ButtonDefaults.buttonColors()
            else ButtonDefaults.outlinedButtonColors()
        ) {
            Text("Kannada")
        }
    }
}