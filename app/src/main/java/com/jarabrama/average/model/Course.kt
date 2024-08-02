package com.jarabrama.average.model

import androidx.compose.runtime.Stable

@Stable
data class Course(val id: Int, var name: String, var credits: Int)