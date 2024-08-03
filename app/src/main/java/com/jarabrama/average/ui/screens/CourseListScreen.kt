@file:OptIn(ExperimentalMaterial3Api::class)

package com.jarabrama.average.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CourseListScreen(
    viewModel: CourseListViewModel,
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val courses by viewModel.courses.collectAsState(initial = null)
    val currentAverages by viewModel.currentAverages.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    val showButtonSheet = remember { mutableStateOf(false) }
    val onButtonSheet = { showButtonSheet.value = !showButtonSheet.value }
    val currentCreditAverage by viewModel.currentCreditAverage.collectAsState()
    val analysis by viewModel.analysis.collectAsState()

    CourseListScreen(
        courses,
        navController,
        viewModel::onDeleteCourse,
        currentAverages,
        bottomSheetState,
        showButtonSheet,
        onButtonSheet,
        analysis,
        currentCreditAverage,
        animatedVisibilityScope
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CourseListScreen(
    courseList: List<Course>?,
    navController: NavController,
    onDeleteCourse: (Int) -> Unit,
    currentAverages: Map<Int, String>,
    buttonSheetState: SheetState,
    showButtonSheet: MutableState<Boolean>,
    onButtonSheet: () -> Unit,
    analysis: String,
    currentCreditAverage: String,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
            onDeleteCourse,
            animatedVisibilityScope
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
fun CoursesTopBar(
    title: String,
    navController: NavController,
    onButtonSheet: () -> Unit,
    ) {
    LargeTopAppBar(
        title = { Text(text = title, overflow = TextOverflow.Ellipsis) },
        actions = {
            IconButton(onClick = { newCourse(navController) }) {
                Icon(imageVector = Icons.Default.Add, "Add course")
            }
            IconButton(onClick = { onButtonSheet() }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "details")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(),
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ListCourses(
    courses: List<Course>?,
    paddingValues: PaddingValues,
    navController: NavController,
    currentAverages: Map<Int, String>,
    onDeleteCourse: (Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    if (courses != null) {
        val isMenu = remember { mutableStateOf(false) }
        val courseName = remember { mutableStateOf<String?>(null) }
        val id = remember { mutableStateOf<Int?>(null) }
        val onMenu = { isMenu.value = !isMenu.value }
        val onIdChange = { it: Int -> id.value = it }
        val onCourseNameChange = { it: String -> courseName.value = it }
        LazyColumn(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            courses.forEach {
                item {
                    currentAverages[it.id]?.let { average ->
                        ItemCourse(
                            course = it,
                            navController = navController,
                            currentAverage = average,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onCourseNameChange = onCourseNameChange,
                            onMenu = onMenu,
                            onIdChange = onIdChange
                        )
                    }
                }
            }
        }
        if (isMenu.value) {
            AlertDialog(
                onDismissRequest = { onMenu() },
                confirmButton = {
                    TextButton(onClick = { onDeleteCourse(id.value ?: -1); onMenu() }) {
                        Text(
                            text = stringResource(id = R.string.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                title = { Text(text = "Delete ${courseName.value ?: ""}") },
                text = { Text(text = "Do you want do delete ${courseName.value ?: ""}") }
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun SharedTransitionScope.ItemCourse(
    course: Course,
    navController: NavController,
    currentAverage: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onCourseNameChange: (String) -> Unit,
    onMenu: () -> Unit,
    onIdChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(Padding.cardList)
            .sharedElement(
                state = rememberSharedContentState(key = "card-${course.id}"),
                animatedVisibilityScope = animatedVisibilityScope
            )
            .combinedClickable(
                onClick = {
                    onClickCourse(navController, course.id)
                },
                onLongClick = {
                    onMenu()
                    onCourseNameChange(course.name)
                    onIdChange(course.id)
                }
            )
        ) {
        Column(
            Modifier
                .padding(Padding.normalPadding)
                .fillMaxWidth()
        ) {

            Column {
                Text(
                    text = course.name,
                    fontSize = FontSizes.normal,
                    modifier = Modifier
                        .padding(Padding.normalPadding)
                        .sharedElement(
                            state = rememberSharedContentState(key = "course-${course.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,

                            ),
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

fun onClickCourse(navController: NavController, id: Int) {
    navController.navigate(Screen.ExpandedCourseScreen(id))
}

