package com.jarabrama.average.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.jarabrama.average.Screen
import com.jarabrama.average.ui.navigation.BottomNavigationItem

@Composable
fun AverageApp() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) {
        AppNavHost(navController = navController, it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomBar(navController: NavController) {
    val navigationItems: List<BottomNavigationItem> = listOf(
        BottomNavigationItem.Courses,
        BottomNavigationItem.Grades,
        BottomNavigationItem.Settings
    )

    var selectedItem by rememberSaveable { mutableStateOf(0) }

    NavigationBar {
        navigationItems.forEachIndexed { index, bottomNavigationItem ->
            NavigationBarItem(
                selected = index == selectedItem,
                onClick = {
                    selectedItem = index;
                    navController.navigate(bottomNavigationItem.route)
                },
                label = {
                    Text(text = bottomNavigationItem.title)
                },
                icon = {
                    Icon(
                        imageVector = if (index == selectedItem) {
                            ImageVector.vectorResource(bottomNavigationItem.selectedIcon)
                        } else ImageVector.vectorResource(bottomNavigationItem.unselectedIcon),
                        contentDescription = bottomNavigationItem.title
                    )
                }
            )

        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, paddingValues: PaddingValues) {

    NavHost(navController = navController, startDestination = Screen.Course.route) {
        navigation(route = Screen.Course.route, startDestination = Screen.CourseList.route) {
            composable(route = Screen.CourseList.route) {
                CourseListScreen(
                    viewModel = hiltViewModel(),
                    navController = navController,
                    paddingValues
                )
            }
            composable(route = Screen.NewCourse.route) {
                NewCourseScreen(viewModel = hiltViewModel(), navController = navController)
            }
        }
        composable(route = Screen.GradeList.route) {
            GradeListScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                paddingValues = paddingValues
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                paddingValues
            )
        }


    }
}