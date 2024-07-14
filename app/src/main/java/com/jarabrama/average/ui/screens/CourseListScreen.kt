package com.jarabrama.average.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
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
fun CourseListScreen(viewModel: CourseListViewModel, navController: NavController) {
    val courses by viewModel.courses.collectAsState()
    CourseListScreen(courseList = courses, navController)
}

@Composable
private fun CourseListScreen(courseList: List<Course>, navController: NavController) {
    Scaffold(
        topBar = { CoursesTopBar(title = stringResource(id = R.string.courses)) },

        floatingActionButton = {
            AddFloatingButton(
                stringResource(R.string.add_course),
                navController
            )
        }
    ) {
        ListCourses(courseList, it)
    }
}

@Composable
fun AddFloatingButton(label: String, navController: NavController) {
    FloatingActionButton(onClick = { newCourse(navController) }) {
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

@Preview(showSystemUi = true)
@Composable
private fun TopBarPreview() {
    CoursesTopBar(title = stringResource(R.string.courses))
}

@Composable
fun CoursesTopBar(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .systemBarsPadding()
        ) {
            Text(
                text = title,
                fontSize = FontSizes.normal,
                modifier = Modifier.padding(Padding.normalPadding)
            )

        }
    }
}


@Composable
fun ListCourses(courses: List<Course>, paddingValues: PaddingValues) {
    Column(
        Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        courses.forEach {
            ItemCourse(it)
        }
    }
}

@Preview
@Composable
private fun ItemCoursePreview() {
    val course = Course(3, "Python fundamentals", 4)
    ItemCourse(course = course)
}

@Composable
fun ItemCourse(course: Course) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(Padding.bigPadding)
    ) {
        Column(
            Modifier
                .padding(Padding.normalPadding)
                .fillMaxWidth()
        ) {
            Text(text = course.name, fontSize = FontSizes.big, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.padding(Padding.smallPadding))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = R.drawable.little_star), null)
                Spacer(modifier = Modifier.padding(Padding.littlePadding))
                Text(text = stringResource(R.string.credits_card), fontSize = FontSizes.normal)
                Spacer(modifier = Modifier.padding(Padding.smallPadding))
                Text(text = "${course.credits}", fontSize = FontSizes.normal)
            }
        }
    }
}