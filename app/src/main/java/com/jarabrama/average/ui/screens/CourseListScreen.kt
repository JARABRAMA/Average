package com.jarabrama.average.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jarabrama.average.R
import com.jarabrama.average.Screen
import com.jarabrama.average.model.Course
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.CourseListViewModel

fun newCourse(navController: NavController) {
    navController.navigate(Screen.NewCourse.route)
}

@Composable
fun CourseListScreen(
    viewModel: CourseListViewModel,
    navController: NavController,
    paddingValues: PaddingValues
) {
    val courses by viewModel.courses.collectAsState()
    CourseListScreen(courseList = courses, navController, paddingValues)
}

@Composable
private fun CourseListScreen(
    courseList: List<Course>,
    navController: NavController,
    paddingValues: PaddingValues
) {
    Scaffold(
        topBar = { CoursesTopBar(title = stringResource(id = R.string.courses), navController) },
        floatingActionButton = {
            AddFloatingButton(
                stringResource(R.string.add_course),
                navController,
                paddingValues
            )
        }
    ) {
        ListCourses(courseList, it, navController)
    }
}

@Composable
fun AddFloatingButton(label: String, navController: NavController, paddingValues: PaddingValues) {
    FloatingActionButton(
        onClick = { newCourse(navController) },
        modifier = Modifier.padding(paddingValues)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Padding.smallPadding)
        ) {
            Icon(painterResource(id = R.drawable.add), null)
            Spacer(modifier = Modifier.padding(Padding.littlePadding))
            Text(text = label)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesTopBar(title: String, navController: NavController) {
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            IconButton(onClick = { newCourse(navController) }) {
                Icon(imageVector = Icons.Default.Add, "Add course")
            }
            IconButton(onClick = { /* Todo: show the average and the analysis of the semester */ }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "details")
            }
        }
    )
}


@Composable
fun ListCourses(courses: List<Course>, paddingValues: PaddingValues, navController: NavController) {
    Column(
        Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        courses.forEach {
            ItemCourse(it, navController)
        }
    }
}

@Preview
@Composable
private fun ItemCoursePreview() {
    val course = Course(3, "Python fundamentals", 4)
    ItemCourse(course = course, rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCourse(course: Course, navController: NavController) {
    Card(
        modifier = Modifier.padding(Padding.cardList),
        shape = RoundedCornerShape(8),
        onClick = { onClickCourse(navController, course.id) }
    ) {
        Column(
            Modifier
                .padding(Padding.normalPadding)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.book),
                    null,
                    modifier = Modifier.padding(Padding.normalPadding)
                )
                Column {

                    Text(
                        text = course.name,
                        fontSize = FontSizes.normal,
                        modifier = Modifier.padding(Padding.normalPadding),
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        Modifier
                            .padding(Padding.horizontal)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Text(text = stringResource(id = R.string.credits))
                            Text(text = ": ${course.credits}")
                        }
                        Row {
                            Text(text = stringResource(id = R.string.average))
                            // Todo: set the average as a text in this place
                        }

                    }
                }
            }
        }
    }
}

fun onClickCourse(navController: NavController, id: Int) {
    navController.navigate("expanded-course/${id.toString()}")
}

