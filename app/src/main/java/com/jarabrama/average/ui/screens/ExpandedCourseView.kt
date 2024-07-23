package com.jarabrama.average.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.Screen
import com.jarabrama.average.model.Grade
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.ExpandedCourseViewModel

@Composable
fun ExpandedCourseScreen(
    viewModel: ExpandedCourseViewModel,
    navController: NavController,
    parentPaddingValues: PaddingValues
) {
    val grades by viewModel.grades.collectAsState()
    val course by viewModel.course.collectAsState()

    ExpandedCourseScreen(grades, parentPaddingValues, navController, course.name, course.id)
}

@Composable
fun ExpandedCourseScreen(
    grades: List<Grade>,
    parentPadding: PaddingValues,
    navController: NavController,
    courseName: String,
    courseId: Int
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarExpandedCourse(courseName, navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewGrade(navController, courseId) },
                modifier = Modifier.padding(parentPadding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(Padding.normalPadding)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.padding(Padding.smallPadding))
                    Text(text = stringResource(R.string.add_grade))
                }
            }
        }
    ) {
        GradeList(grades, it)
    }
}

fun onNewGrade(navController: NavController, courseId: Int) {
    navController.navigate(Screen.NewGradeScreen(courseId))
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBarExpandedCourse(courseName: String, navController: NavController) {
    TopAppBar(
        title = { Text(text = courseName) },
        navigationIcon = {
            IconButton(
                onClick = { onBack(navController) }) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Go back"
                )
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "analysis")
            }
        }
    )
}

@Composable
private fun GradeList(grades: List<Grade>, paddingValues: PaddingValues) {
    Column(Modifier.padding(paddingValues)) {
        grades.forEach {
            GradeItem(name = it.name, qualification = it.qualification, percentage = it.percentage)
        }
    }
}
