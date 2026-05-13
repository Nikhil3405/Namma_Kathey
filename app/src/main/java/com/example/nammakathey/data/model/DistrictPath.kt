package com.example.nammakathey.data.model

import androidx.compose.ui.graphics.Path

data class DistrictPath(
    val name: String,
    val pathData: String,
    var path: Path? = null
)