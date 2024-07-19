package com.jarabrama.average

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    object CourseListScreen

    @Serializable
    object GradeListScreen

    @Serializable
    object SettingsScreen

    @Serializable
    object NewCourseScreen

    @Serializable
    data class ExpandedCourseScreen(val courseId: Int)

    @Serializable
    data class NewGradeScreen(val courseId: Int)

    @Serializable
    object CourseScreen
}


