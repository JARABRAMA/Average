package com.jarabrama.average.ui.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jarabrama.average.Screen
import com.jarabrama.average.di.ExpandedCourseAssistedFactory
import com.jarabrama.average.di.NewGradeAssistedFactory
import com.jarabrama.average.ui.navigation.BottomNavigationItem
import com.jarabrama.average.ui.viewmodel.ExpandedCourseViewModel
import com.jarabrama.average.ui.viewmodel.NewGradeViewModel

@Composable
fun AverageApp(
    expandedCourseAssistedFactory: ExpandedCourseAssistedFactory,
    newGradeAssistedFactory: NewGradeAssistedFactory
) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) {
        AppNavHost(navController = navController, it, expandedCourseAssistedFactory, newGradeAssistedFactory)
    }
}

@Composable
private fun BottomBar(navController: NavController) {
    val navigationItems: List<BottomNavigationItem> = listOf(
        BottomNavigationItem.Courses,
        BottomNavigationItem.Grades,
        BottomNavigationItem.Settings
    )

    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    expandedCourseAssistedFactory: ExpandedCourseAssistedFactory,
    newGradeAssistedFactory: NewGradeAssistedFactory
) {
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = Screen.CourseScreen, modifier = Modifier.padding(innerPadding)) {
            navigation<Screen.CourseScreen>(startDestination = Screen.CourseListScreen) {
                composable<Screen.CourseListScreen> {
                    CourseListScreen(
                        viewModel = hiltViewModel(),
                        navController = navController,
                        animatedVisibilityScope = this@composable
                    )
                }
                composable<Screen.NewCourseScreen> {
                    NewCourseScreen(viewModel = hiltViewModel(), navController = navController)
                }
                composable<Screen.ExpandedCourseScreen> {
                    val args = it.toRoute<Screen.ExpandedCourseScreen>()
                    val viewModel: ExpandedCourseViewModel =
                        expandedCourseAssistedFactory.create(args.courseId)
                    ExpandedCourseScreen(
                        viewModel = viewModel,
                        navController = navController,
                        animatedVisibilityScope = this@composable
                    )
                }
                composable<Screen.NewGradeScreen> {
                    val args = it.toRoute<Screen.NewGradeScreen>()
                    val viewModel: NewGradeViewModel =
                        newGradeAssistedFactory.create(args.courseId)
                    NewGradeScreen(viewModel, navController)
                }
            }
            composable<Screen.GradeListScreen> {
                GradeListScreen(hiltViewModel())
            }
            composable<Screen.SettingsScreen> {
                SettingsScreen(
                    navController = navController,
                    viewModel = hiltViewModel()
                )
            }
        }
    }
}