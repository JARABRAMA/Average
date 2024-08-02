package com.jarabrama.average.model

import androidx.compose.runtime.Stable

@Stable
data class Grade(
    val id: Int,
    val courseId: Int,
    val name: String,
    val qualification: Double,
    val percentage: Double
)