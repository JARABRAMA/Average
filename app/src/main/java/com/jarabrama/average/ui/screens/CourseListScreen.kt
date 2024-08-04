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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.Screen
import com.jarabrama.average.model.Course
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.CourseListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    val editName by viewModel.editName.collectAsState()
    val editCredits by viewModel.editCredits.collectAsState()


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
        animatedVisibilityScope,
        editName,
        editCredits,
        viewModel.onEditNameChange,
        viewModel.onEditCreditChange,
        viewModel.setValues,
        viewModel.onEditCourse,
        viewModel.getSnackbarStatus,
        viewModel.getSnackbarMessage,
        viewModel.onDismissSnackbar
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
    animatedVisibilityScope: AnimatedVisibilityScope,
    editName: String,
    editCredits: String,
    onEditNameChange: (String) -> Unit,
    onEditCreditChange: (String) -> Unit,
    setValues: (String, String) -> Unit,
    onEditCourse: (Int) -> Any,
    getSnackbarStatus: () -> Boolean,
    getSnackbarMessage: () -> String,
    onDismissSnackbar: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope()
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (courseList != null) {
            var pickedCourse by remember { mutableStateOf<Course?>(null) }
            val pickCourse = { course: Course -> pickedCourse = course }

            var showOptions by remember { mutableStateOf(false) }
            val onShowOptions = { showOptions = !showOptions }

            var showEditForm by remember { mutableStateOf(false) }
            val onShowEditForm = { showEditForm = !showEditForm }
            ListCourses(
                courseList,
                paddingValues,
                navController,
                currentAverages,
                animatedVisibilityScope,
                pickCourse,
                onShowOptions
            )
            if (showButtonSheet.value) {
                AnalysisBottomSheet(onButtonSheet, buttonSheetState, analysis, currentCreditAverage)
            }
            if (showOptions) {
                pickedCourse?.let { course ->
                    OptionsModalSheet(
                        onShowOptions,
                        pickedCourse,
                        onShowEditForm,
                        onDeleteCourse,
                        course.id
                    )
                }
            }
            if (showEditForm) {
                pickedCourse?.let { course ->
                    EditCourseModalSheet(
                        onShowEditForm,
                        course,
                        editName,
                        onEditNameChange,
                        editCredits,
                        onEditCreditChange,
                        onEditCourse,
                        getSnackbarStatus,
                        scope,
                        snackbarHostState,
                        getSnackbarMessage,
                        onDismissSnackbar
                    )
                }
            }
            LaunchedEffect(key1 = showEditForm) {
                pickedCourse?.let { course ->
                    setValues(course.name, course.credits.toString())
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EditCourseModalSheet(
    onShowEditForm: () -> Unit,
    it: Course,
    editName: String,
    onEditNameChange: (String) -> Unit,
    editCredits: String,
    onEditCreditChange: (String) -> Unit,
    onEditCourse: (Int) -> Any,
    getSnackbarStatus: () -> Boolean,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    getSnackbarMessage: () -> String,
    onDismissSnackbar: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = { onShowEditForm() }) {
        Text(
            text = it.name,
            fontSize = FontSizes.normal,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(Padding.normalPadding)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.padding(Padding.vertical))
        Text(
            stringResource(R.string.course_name),
            modifier = Modifier
                .fillMaxWidth(.8f)
                .align(Alignment.CenterHorizontally)
        )
        TextField(
            value = editName,
            onValueChange = onEditNameChange,
            modifier = Modifier
                .padding(Padding.cardList)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(.8f),
            maxLines = 1,
            singleLine = true,
        )
        Text(
            stringResource(R.string.credits),
            modifier = Modifier
                .fillMaxWidth(.8f)
                .align(Alignment.CenterHorizontally)
        )
        TextField(
            value = editCredits,
            onValueChange = onEditCreditChange,
            modifier = Modifier
                .padding(Padding.cardList)
                .fillMaxWidth(.8f)
                .align(Alignment.CenterHorizontally),
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(Modifier.padding(Padding.cardList))
        Button(
            onClick = {
                onEditCourse(it.id)
                snackbar(
                    getSnackbarStatus,
                    scope,
                    snackbarHostState,
                    getSnackbarMessage,
                    onDismissSnackbar
                )
                onShowEditForm()
            },
            modifier = Modifier
                .fillMaxWidth(.8f)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.save))
        }
    }
}

private fun snackbar(
    getSnackbarStatus: () -> Boolean,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    getSnackbarMessage: () -> String,
    onDismissSnackbar: () -> Unit
) {
    if (getSnackbarStatus()) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = getSnackbarMessage(),
                withDismissAction = true
            )
            onDismissSnackbar()
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AnalysisBottomSheet(
    onButtonSheet: () -> Unit,
    buttonSheetState: SheetState,
    analysis: String,
    currentCreditAverage: String
) {
    ModalBottomSheet(onDismissRequest = onButtonSheet, sheetState = buttonSheetState) {
        Text(
            text = "Current Credit Average: $currentCreditAverage",
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.bigPadding)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = FontSizes.normal,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = analysis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.bigPadding)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = FontSizes.normal
        )

    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun OptionsModalSheet(
    onShowOptions: () -> Unit,
    pickedCourse: Course?,
    onShowEditForm: () -> Unit,
    onDeleteCourse: (Int) -> Unit,
    courseId: Int
) {
    ModalBottomSheet(onDismissRequest = { onShowOptions() }) {
        Text(
            text = pickedCourse?.name ?: "",
            fontWeight = FontWeight.Bold,
            fontSize = FontSizes.normal,
            modifier = Modifier
                .padding(Padding.cardList)
                .align(Alignment.CenterHorizontally)
        )
        Button(
            onClick = { onShowEditForm(); onShowOptions() },
            modifier = Modifier
                .fillMaxWidth(.8f)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.Edit, stringResource(R.string.edit))
            Spacer(Modifier.padding(Padding.horizontal))
            Text(stringResource(R.string.edit))
        }
        Button(
            onClick = { onDeleteCourse(courseId); onShowOptions() },
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth(.8f)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.Delete, stringResource(R.string.delete))
            Spacer(Modifier.padding(Padding.horizontal))
            Text(stringResource(R.string.delete))
        }
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
    courses: List<Course>,
    paddingValues: PaddingValues,
    navController: NavController,
    currentAverages: Map<Int, String>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    pickCourse: (Course) -> Unit,
    onShowOptions: () -> Unit
) {
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
                        pickCourse = pickCourse,
                        onShowOptions
                    )
                }
            }
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
    pickCourse: (Course) -> Unit,
    onShowOptions: () -> Unit,
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
                    pickCourse(course)
                    onShowOptions()
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

