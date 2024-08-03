package com.jarabrama.average.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.Screen
import com.jarabrama.average.model.Grade
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.ExpandedCourseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val showBottomSheet = remember { mutableStateOf(false) }
    val onShowBottomSheet = { showBottomSheet.value = !showBottomSheet.value }
    val currentAverage by viewModel.average.collectAsState()
    val qualificationValue by viewModel.qualification.collectAsState()
    val percentage by viewModel.percentage.collectAsState()
    val editGradeName by viewModel.editGradeName.collectAsState()

    ExpandedCourseScreen(
        grades,
        navController,
        courseName,
        course?.id ?: -1,
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
        animatedVisibilityScope,
        viewModel.onDelete,
        viewModel.onEdit,
        qualificationValue,
        viewModel.onQualificationChange,
        percentage,
        viewModel.onPercentageChange,
        editGradeName,
        viewModel.onEditNameChange,
        viewModel.setEditGradeValues
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ExpandedCourseScreen(
    grades: List<Grade>?,
    navController: NavController,
    courseName: String,
    courseId: Int,
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
    animatedVisibilityScope: AnimatedVisibilityScope,
    onDeleteGrade: (Int) -> Job,
    onEditGrade: (Int) -> Job,
    qualificationValue: String,
    onQualificationChange: (String) -> Unit,
    percentageValue: String,
    onPercentageChange: (String) -> Unit,
    editGradeName: String,
    onEditGradeNameChange: (String) -> Unit,
    setEditGradeValues: (String, String, String) -> Unit
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
    ) { paddingValues ->
        var showContent by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = Unit) {
            delay(1000)
            showContent = true
        }
        if (grades != null && showContent) {
            var showSheetOptions by remember { mutableStateOf(false) }
            val onShowSheetOptions = { showSheetOptions = !showSheetOptions }
            var showEditGrade by remember { mutableStateOf(false) }
            val onShowEditGrade = { showEditGrade = !showEditGrade }
            var pickedGrade by remember { mutableStateOf<Grade?>(null) }
            val onChangePickedGrade = { grade: Grade -> pickedGrade = grade }

            val editSheetButtonSheetState = rememberModalBottomSheetState()
            GradeList(
                grades,
                paddingValues,
                onChangePickedGrade,
                onShowSheetOptions
            )

            if (showBottomSheet.value) {
                AnalysisBottomSheet(
                    onBottomSheet,
                    bottomSheetState,
                    getBottomSheetContent,
                    currentAverage
                )
            }
            if (showSheetOptions) {
                BottomSheetOptions(
                    onShowSheetOptions,
                    pickedGrade,
                    onShowEditGrade,
                    onDeleteGrade
                )
            }
            if (showEditGrade) {
                BottomSheetEditGrade(
                    onShowEditGrade,
                    pickedGrade,
                    editGradeName,
                    onEditGradeNameChange,
                    qualificationValue,
                    onQualificationChange,
                    percentageValue,
                    onPercentageChange,
                    onEditGrade,
                    getErrorState,
                    getErrorMessage,
                    onDismissSnackbar,
                    scope,
                    snackbarHostState,
                    editSheetButtonSheetState
                )
            }

            LaunchedEffect(showEditGrade) {
                pickedGrade?.let {
                    setEditGradeValues(it.name, it.qualification.toString(), it.percentage.toString())
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BottomSheetEditGrade(
    onShowEditGrade: () -> Unit,
    grade: Grade?,
    editNameValue: String,
    onNameChange: (String) -> Unit,
    qualificationValue: String,
    onQualificationChange: (String) -> Unit,
    percentageValue: String,
    onPercentageChange: (String) -> Unit,
    onEditGrade: (Int) -> Job,
    getErrorState: () -> Boolean,
    getErrorMessage: () -> String,
    onDismissSnackbar: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    editGradeBottomSheet: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = { onShowEditGrade() },
        sheetState = editGradeBottomSheet
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = grade?.name ?: "",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = FontSizes.normal,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.padding(Padding.cardList))

            Text(
                text = stringResource(id = R.string.grade_name),
                Modifier.padding(Padding.cardList)
            )
            TextField(
                value = editNameValue,
                onValueChange = onNameChange,
                maxLines = 1,
                modifier = Modifier.padding(Padding.cardList)
            )
            Text(
                text = stringResource(id = R.string.qualification),
                Modifier.padding(Padding.cardList)
            )
            TextField(
                value = qualificationValue,
                onValueChange = onQualificationChange,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.padding(Padding.cardList)
            )
            Text(
                text = stringResource(id = R.string.percentage),
                Modifier.padding(Padding.cardList)
            )
            TextField(
                value = percentageValue,
                onValueChange = onPercentageChange,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.padding(Padding.cardList)
            )
        }
        Spacer(Modifier.padding(Padding.cardList))
        val keyboard = LocalSoftwareKeyboardController.current
        Button(
            onClick = {
                keyboard?.hide()
                grade?.let { grade -> onEditGrade(grade.id) }
                validatingSnackbar(
                    getErrorState,
                    scope,
                    snackbarHostState,
                    getErrorMessage,
                    onDismissSnackbar
                )
                onShowEditGrade()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(.7f)
        ) {
            Text(
                text = stringResource(id = R.string.save),
                textAlign = TextAlign.End
            )
        }
        Spacer(Modifier.padding(Padding.bigPadding))
    }
}


private fun validatingSnackbar(
    getErrorState: () -> Boolean,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    getErrorMessage: () -> String,
    onDismissSnackbar: () -> Unit
) {
    if (getErrorState()) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = getErrorMessage(),
                duration = SnackbarDuration.Short,
                withDismissAction = true,
            )
            onDismissSnackbar()
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AnalysisBottomSheet(
    onBottomSheet: () -> Unit,
    bottomSheetState: SheetState,
    getBottomSheetContent: () -> String,
    currentAverage: String
) {
    ModalBottomSheet(
        onDismissRequest = { onBottomSheet() },
        sheetState = bottomSheetState
    ) {
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
            text = getBottomSheetContent(),
            modifier = Modifier
                .padding(Padding.bigPadding)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = FontSizes.normal
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BottomSheetOptions(
    onShowSheetOptions: () -> Unit,
    grade: Grade?,
    onShowEditGrade: () -> Unit,
    onDeleteGrade: (Int) -> Job,
) {
    ModalBottomSheet(
        onDismissRequest = { onShowSheetOptions() },
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                grade?.name ?: "",
                Modifier
                    .fillMaxWidth()
                    .padding(Padding.normalPadding),
                fontSize = FontSizes.normal,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { onShowEditGrade(); onShowSheetOptions() },
                modifier = Modifier
                    .padding(Padding.cardList)
                    .fillMaxWidth(.8f)
            ) {
                Icon(Icons.Default.Edit, "edit")
                Spacer(Modifier.padding(Padding.horizontal))
                Text(text = "Edit")
            }
            Button(
                onClick = { grade?.let { onDeleteGrade(it.id); onShowSheetOptions() } },
                modifier = Modifier
                    .padding(Padding.cardList)
                    .fillMaxWidth(.8f),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, "delete")
                Spacer(Modifier.padding(Padding.horizontal))
                Text(text = "Delete")
            }
        }
    }
}

fun onNewGrade(navController: NavController, courseId: Int) {
    navController.navigate(Screen.NewGradeScreen(courseId))
}


@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
private fun SharedTransitionScope.TopBarExpandedCourse(
    courseName: String,
    navController: NavController,
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
            IconButton(onClick = { onBottomSheet() }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "analysis")
            }
        }
    )
}

@Composable
private fun GradeList(
    grades: List<Grade>,
    paddingValues: PaddingValues,
    onChangePickedGrade: (Grade) -> Unit,
    onShowDialog: () -> Unit
) {
    LazyColumn(Modifier.padding(paddingValues)) {
        items(grades.size, key = { grades[it].id }) {
            GradeItem(
                grades[it],
                onChangePickedGrade,
                onShowDialog
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GradeItem(
    grade: Grade,
    onChangePickedGrade: (Grade) -> Unit,
    onShowDialog: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(Padding.cardList)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    onChangePickedGrade(grade)
                    onShowDialog()
                }
            )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(Padding.normalPadding)
        ) {
            Text(
                text = grade.name,
                modifier = Modifier.padding(Padding.smallPadding),
                fontSize = FontSizes.normal,
                fontWeight = FontWeight.Bold
            )
            BoxWithConstraints {
                if (maxWidth > 300.dp) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LabelsGrade(grade.qualification, grade.percentage)
                    }
                } else {
                    Column {
                        LabelsGrade(grade.qualification, grade.percentage)
                    }
                }
            }
        }
    }
}
