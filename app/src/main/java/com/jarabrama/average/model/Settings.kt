package com.jarabrama.average.model

import androidx.compose.runtime.Stable

@Stable
data class Settings(
    var maxQualification: Double = 5.0,
    var minQualification: Double = 0.0,
    var goal: Double = 3.0
)