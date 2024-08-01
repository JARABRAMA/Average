package com.jarabrama.average.ui.screens

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.PopupProperties
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    viewModel: CourseListViewModel,
    navController: NavController,
    paddingValues: PaddingValues,
) {
    val courses by viewModel.courses.collectAsState()
    val currentAverages by viewModel.currentAverages.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    val showButtonSheet = remember { mutableStateOf(false) }
    val onButtonSheet = { showButtonSheet.value = !showButtonSheet.value }
    val currentCreditAverage by viewModel.currentCreditAverage.collectAsState()
    val analysis by viewModel.analysis.collectAsState()

    CourseListScreen(
        courses,
        navController,
        paddingValues,
        viewModel::onDeleteCourse,
        currentAverages,
        bottomSheetState,
        showButtonSheet,
        onButtonSheet,
        analysis,
        currentCreditAverage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseListScreen(
    courseList: List<Course>,
    navController: NavController,
    parentPadding: PaddingValues,
    onDeleteCourse: (Int) -> Unit,
    currentAverages: Map<Int, String>,
    buttonSheetState: SheetState,
    showButtonSheet: MutableState<Boolean>,
    onButtonSheet: () -> Unit,
    analysis: String,
    currentCreditAverage: String
) {
    val scaffoldPadding = PaddingValues(bottom = parentPadding.calculateBottomPadding())
    Scaffold(
        modifier = Modifier.padding(scaffoldPadding),
        topBar = {
            CoursesTopBar(
                title = stringResource(id = R.string.courses),
                navController = navController,
                onButtonSheet = onButtonSheet
            )
        },
        floatingActionButton = {
            AddFloatingButton(
                stringResource(R.string.add_course),
                navController

            )
        }
    ) {
        ListCourses(
            courseList,
            it,
            navController,
            currentAverages,
            onDeleteCourse
        )
        if (showButtonSheet.value) {
            ModalBottomSheet(onDismissRequest = onButtonSheet, sheetState = buttonSheetState) {
                CreditAverageBottomSheet(analysis, currentCreditAverage)
            }
        }
    }
}

@Composable
fun CreditAverageBottomSheet(analysis: String, currentCreditAverage: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Current Credit Average: $currentCreditAverage",
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.bigPadding),
            textAlign = TextAlign.Center,
            fontSize = FontSizes.normal,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = analysis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.bigPadding),
            textAlign = TextAlign.Center,
            fontSize = FontSizes.normal
        )

    }
}

@Composable
fun AddFloatingButton(label: String, navController: NavController) {
    FloatingActionButton(
        onClick = { newCourse(navController) },
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
fun CoursesTopBar(title: String, navController: NavController, onButtonSheet: () -> Unit) {
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            IconButton(onClick = { newCourse(navController) }) {
                Icon(imageVector = Icons.Default.Add, "Add course")
            }
            IconButton(onClick = { onButtonSheet() }) {
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
    currentAverages: Map<Int, String>,
    onDeleteCourse: (Int) -> Unit
) {
    LazyColumn(
        Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        items(courses.size) {
            val course = courses[it]

            ItemCourse(
                course = course,
                navController = navController,
                onDeleteCourse = onDeleteCourse,
                currentAverage = currentAverages[course.id] ?: "0"
            )

        }
    }
}

@Composable
fun ItemCourse(
    course: Course,
    navController: NavController,
    onDeleteCourse: (Int) -> Unit,
    currentAverage: String

) {

    var pressOffset = remember { mutableStateOf(DpOffset.Zero) }
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
                        pressOffset.value = DpOffset(it.x.toDp(), it.y.toDp())
                        Log.i("PressOffset", pressOffset.value.toString())
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
                            Text(text = ": $currentAverage")
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
        offset = pressOffset.value,
        properties = PopupProperties(
        )
    ) {
        Log.i("Offset", pressOffset.value.toString())
        PopUpMenu(onDeleteCourse, course.id, isMenu)
    }
}


@Preview
@Composable
private fun PopupMenu() {
    val preview = remember { mutableStateOf(true) }
    PopUpMenu(onDeleteCourse = {}, courseId = 0, preview)
}

@Composable
private fun PopUpMenu(
    onDeleteCourse: (Int) -> Unit,
    courseId: Int,
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
        }
    }
}

fun onClickCourse(navController: NavController, id: Int) {
    navController.navigate(Screen.ExpandedCourseScreen(id))
}

