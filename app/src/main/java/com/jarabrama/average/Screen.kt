package com.jarabrama.average

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    data object Course : Screen("course")
    data object CourseList : Screen("course-list")

    data class ExpandedCourse(val courseId: String? = "course-id") : Screen(
        "expanded-course/{$courseId}",
        listOf(navArgument(courseId!!) { type = NavType.IntType })
    )

    data object NewCourse : Screen("new-course")
    data object GradeList : Screen("grade-list")
    data object Settings : Screen("settings")
}