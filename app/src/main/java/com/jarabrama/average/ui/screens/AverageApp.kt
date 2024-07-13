package com.jarabrama.average.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.jarabrama.average.Screen

@Composable
fun AverageApp() {
    val navController = rememberNavController()
    AppNavHost(navController = navController)
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Course.route) {
        navigation(route = Screen.Course.route, startDestination = Screen.CourseList.route) {
            composable(route = Screen.CourseList.route) {
                CourseListScreen(viewModel = hiltViewModel(), navController = navController)
            }
            composable(route = Screen.NewCourse.route) {
                NewCourseScreen(viewModel = hiltViewModel(), navController = navController)
            }
        }
    }
}