package com.example.nammakathey.data.model

data class DistrictRegion(
    val name: String,
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    val isCompleted: Boolean = false
)