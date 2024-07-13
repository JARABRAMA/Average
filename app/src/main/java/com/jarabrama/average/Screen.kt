package com.jarabrama.average

import androidx.navigation.NavArgument

sealed class Screen(
    val route: String,
    val navArguments: List<NavArgument> = emptyList()
) {
    data object Course: Screen("course")
    data object CourseList: Screen("course-list")
    data object NewCourse: Screen("new-course")
}