package com.jarabrama.average.ui.navigation

import com.jarabrama.average.R
import com.jarabrama.average.Screen

sealed class BottomNavigationItem(
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val route: Any
) {
    data object Courses : BottomNavigationItem(
        title = "Courses",
        selectedIcon = R.drawable.backpack_filled,
        unselectedIcon = R.drawable.backpack,
        route = Screen.CourseScreen
    )
    data object Grades: BottomNavigationItem (
        title = "Grades",
        selectedIcon = R.drawable.article_filled,
        unselectedIcon = R.drawable.article,
        route = Screen.GradeListScreen
    )
    data object Settings: BottomNavigationItem (
        title = "Settings",
        selectedIcon = R.drawable.settings_filled,
        unselectedIcon = R.drawable.settings,
        route = Screen.SettingsScreen
    )
}
