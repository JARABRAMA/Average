package com.jarabrama.average.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.Screen
import com.jarabrama.average.model.Grade
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.ExpandedCourseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ExpandedCourseScreen(
    viewModel: ExpandedCourseViewModel,
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val grades by viewModel.grades.collectAsState(initial = null)
    val course by viewModel.course.collectAsState()
    val courseName by viewModel.courseName.collectAsState()
    val showForm = remember { mutableStateOf(false) }
    val nameValue by viewModel.editNameValue.collectAsState()
    val creditValue by viewModel.editCreditValue.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val showBottomSheet = remember { mutableStateOf(false) }
    val onShowBottomSheet = {
        showBottomSheet.value = !showBottomSheet.value
    }
    val currentAverage by viewModel.average.collectAsState()

    ExpandedCourseScreen(
        grades,
        navController,
        courseName,
        course?.id ?: -1,
        showForm,
        nameValue,
        creditValue,
        viewModel::onNameChange,
        viewModel::onCreditChange,
        viewModel::onUpdate,
        snackbarHostState,
        scope,
        viewModel::getErrorMessage,
        viewModel::getErrorState,
        viewModel::onDismissSnackbar,
        bottomSheetState,
        showBottomSheet,
        onShowBottomSheet,
        viewModel::getBottomSheetContent,
        currentAverage,
        animatedVisibilityScope
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ExpandedCourseScreen(
    grades: List<Grade>?,
    navController: NavController,
    courseName: String,
    courseId: Int,
    showForm: MutableState<Boolean>,
    nameValue: String,
    creditValue: String,
    onNameChange: (String) -> Unit,
    onCreditChange: (String) -> Unit,
    onUpdate: () -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    getErrorMessage: () -> String,
    getErrorState: () -> Boolean,
    onDismissSnackbar: () -> Unit,
    bottomSheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    onBottomSheet: () -> Unit,
    getBottomSheetContent: () -> String,
    currentAverage: String,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Scaffold(
        modifier = Modifier
            .sharedElement(
                state = rememberSharedContentState(key = "card-${courseId}"),
                animatedVisibilityScope = animatedVisibilityScope
            ),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBarExpandedCourse(
                courseName,
                navController,
                showForm,
                onBottomSheet,
                animatedVisibilityScope,
                courseId
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewGrade(navController, courseId) },
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
        var showContent by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = Unit) {
            delay(1000)
            showContent = true
        }
        if (showContent) {
            GradeList(
                grades,
                it,
                showForm,
                nameValue,
                creditValue,
                onNameChange,
                onCreditChange,
                onUpdate,
                snackbarHostState,
                scope,
                getErrorMessage,
                getErrorState,
                onDismissSnackbar
            )

            if (showBottomSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { onBottomSheet() },
                    sheetState = bottomSheetState
                ) {
                    BottomSheetContent(message = getBottomSheetContent(), currentAverage)
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

fun onNewGrade(navController: NavController, courseId: Int) {
    navController.navigate(Screen.NewGradeScreen(courseId))
}

@Composable
fun BottomSheetContent(message: String, currentAverage: String) {
    Text(
        text = "Current Average: $currentAverage",
        modifier = Modifier
            .padding(Padding.bigPadding)
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontSize = FontSizes.normal,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = message,
        modifier = Modifier
            .padding(Padding.bigPadding)
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontSize = FontSizes.normal
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
private fun SharedTransitionScope.TopBarExpandedCourse(
    courseName: String,
    navController: NavController,
    showForm: MutableState<Boolean>,
    onBottomSheet: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    courseId: Int
) {

    TopAppBar(
        title = {
            Text(
                text = courseName,
                maxLines = 1,
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState(key = "course-${courseId}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            )
        },
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
            IconButton(onClick = { showForm.value = !showForm.value }) {
                Icon(Icons.Default.Edit, "Edit")
            }
            IconButton(onClick = { onBottomSheet() }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "analysis")
            }
        }
    )
}


@Composable
fun EditCourseForm(
    nameValue: String,
    creditValue: String,
    onNameChange: (String) -> Unit,
    onCreditChange: (String) -> Unit,
    onUpdate: () -> Unit,
    showForm: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    getErrorMessage: () -> String,
    getErrorState: () -> Boolean,
    onDismissSnackbar: () -> Unit
) {
    Card {
        val keyboardController = LocalSoftwareKeyboardController.current
        Column(Modifier.padding(Padding.normalPadding)) {
            Text(
                text = "Edit your course",
                fontSize = FontSizes.big,
                modifier = Modifier.padding(Padding.cardList)
            )
            Text(text = "Course name", Modifier.padding(Padding.cardList))
            OutlinedTextField(
                shape = RoundedCornerShape(20),
                value = nameValue,
                onValueChange = { onNameChange(it) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier.padding(Padding.cardList)
            )
            Text(text = "Credits", Modifier.padding(Padding.cardList))
            OutlinedTextField(
                shape = RoundedCornerShape(20),
                value = creditValue,
                onValueChange = { onCreditChange(it) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier.padding(Padding.cardList),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
        Row(
            modifier = Modifier.padding(Padding.cardList),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                keyboardController?.hide()
                showForm.value = !showForm.value
                onUpdate()
                if (getErrorState()) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = getErrorMessage(),
                            duration = SnackbarDuration.Short
                        )
                        onDismissSnackbar()
                    }
                }
            }
            ) {
                Text(text = "Update Course")
            }
        }
    }
}

@Composable
private fun GradeList(
    grades: List<Grade>?,
    paddingValues: PaddingValues,
    showForm: MutableState<Boolean>,
    nameValue: String,
    creditValue: String,
    onNameChange: (String) -> Unit,
    onCreditChange: (String) -> Unit,
    onUpdate: () -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    getErrorMessage: () -> String,
    getErrorState: () -> Boolean,
    onDismissSnackbar: () -> Unit
) {
    if (grades != null) {
        LazyColumn(Modifier.padding(paddingValues)) {
            items(grades.size) {
                GradeItem(
                    name = grades[it].name,
                    qualification = grades[it].qualification,
                    percentage = grades[it].percentage
                )
            }
        }
    } else {
        CircularProgressIndicator()
    }
    when {
        showForm.value -> {
            Dialog({ showForm.value = !showForm.value }) {
                EditCourseForm(
                    nameValue,
                    creditValue,
                    onNameChange,
                    onCreditChange,
                    onUpdate,
                    showForm,
                    snackbarHostState,
                    scope,
                    getErrorMessage,
                    getErrorState,
                    onDismissSnackbar
                )
            }
        }
    }
}
