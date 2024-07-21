package com.jarabrama.average.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.Screen
import com.jarabrama.average.model.Course
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.CourseListViewModel

fun newCourse(navController: NavController) {
    navController.navigate(Screen.NewCourseScreen)
}

@Composable
fun CourseListScreen(
    viewModel: CourseListViewModel,
    navController: NavController,
    paddingValues: PaddingValues,
) {
    val courses by viewModel.courses.collectAsState()

    CourseListScreen(
        courseList = courses,
        navController,
        paddingValues,
        viewModel::onDeleteCourse,
        viewModel::onEditCourse,
    )
}

@Composable
private fun CourseListScreen(
    courseList: List<Course>,
    navController: NavController,
    paddingValues: PaddingValues,
    onDeleteCourse: (Int) -> Unit,
    onEditCourse: (Int) -> Unit,
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
        ListCourses(
            courseList,
            it,
            navController,
            onDeleteCourse,
            onEditCourse
        )
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
        },
        colors = TopAppBarDefaults.topAppBarColors()
    )
}


@Composable
fun ListCourses(
    courses: List<Course>,
    paddingValues: PaddingValues,
    navController: NavController,
    onDeleteCourse: (Int) -> Unit,
    onEditCourse: (Int) -> Unit
) {
    Column(
        Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        courses.forEach {
            ItemCourse(
                it,
                navController,
                onDeleteCourse,
                onEditCourse
            )
        }
    }
}

@Composable
fun ItemCourse(
    course: Course,
    navController: NavController,
    onDeleteCourse: (Int) -> Unit,
    onEditCourse: (Int) -> Unit,

    ) {

    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    val isMenu = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(Padding.cardList)
            .pointerInput(true) {
                detectTapGestures(
                    onTap = {
                        onClickCourse(navController, course.id)
                    },
                    onLongPress = {
                        // onShowMenu()
                        isMenu.value = true
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    },
                )
            },

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
    DropdownMenu(
        expanded = isMenu.value,
        onDismissRequest = {
            isMenu.value = false
        },
        offset = pressOffset.copy(y = pressOffset.y, x = pressOffset.x),
    ) {
        PopUpMenu(onDeleteCourse, course.id, onEditCourse, isMenu)
    }
}


@Preview
@Composable
private fun PopupMenu() {
    val preview = remember { mutableStateOf(true) }
    PopUpMenu(onDeleteCourse = {}, courseId = 0, onEditCourse = {}, preview)
}

@Composable
private fun PopUpMenu(
    onDeleteCourse: (Int) -> Unit,
    courseId: Int,
    onEditCourse: (Int) -> Unit,
    isMenuVisible: MutableState<Boolean>
) {
    Surface(
        shape = RoundedCornerShape(20),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth()) {
            TextButton(
                onClick = {
                    onDeleteCourse(courseId)
                    isMenuVisible.value = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.padding(Padding.horizontal)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(id = R.string.delete))
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
            HorizontalDivider()
            TextButton(
                onClick = { onEditCourse(courseId); isMenuVisible.value = false },
                Modifier.padding(Padding.horizontal)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(R.string.edit))
                    Icon(Icons.Default.Edit, "Edit")
                }
            }
        }
    }
}

fun onClickCourse(navController: NavController, id: Int) {
    navController.navigate(Screen.ExpandedCourseScreen(id))
}

