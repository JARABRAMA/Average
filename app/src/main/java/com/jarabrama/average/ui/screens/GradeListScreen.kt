package com.jarabrama.average.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jarabrama.average.R
import com.jarabrama.average.model.Grade
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.GradeListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeListScreen(
    viewModel: GradeListViewModel,
    paddingValues: PaddingValues
) {
    val grades by viewModel.grades.collectAsState()
    val modalBottomSheetState = rememberModalBottomSheetState()
    val showBottomSheet = remember { mutableStateOf(false) }
    val onBottomSheet = { showBottomSheet.value = !showBottomSheet.value }
    val average by viewModel.average.collectAsState()

    GradeListScreen(
        grades = grades,
        paddingValues,
        modalBottomSheetState,
        showBottomSheet,
        onBottomSheet,
        average
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeListScreen(
    grades: List<Grade>,
    parentPadding: PaddingValues,
    modalBottomSheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    onBottomSheet: () -> Unit,
    average: String
) {

    val scaffoldPadding = PaddingValues(bottom = parentPadding.calculateBottomPadding())
    Scaffold(
        modifier = Modifier.padding(scaffoldPadding),
        topBar = {
            ListGradesTopBar(onBottomSheet)
        }
    ) { paddingValues ->
        ListGrades(grades, paddingValues)
        if (showBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { onBottomSheet() },
                sheetState = modalBottomSheetState
            ) {
                GradListAnalysis(average)
            }
        }
    }
}

@Composable
private fun GradListAnalysis(average: String) {
    Text(
        text = "Simple average",
        modifier = Modifier
            .padding(Padding.bigPadding)
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontSize = FontSizes.normal,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "In all yours qualifications you have an average of $average",
        modifier = Modifier
            .padding(Padding.bigPadding)
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontSize = FontSizes.normal
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ListGradesTopBar(onBottomSheet: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.grades)) },
        actions = {
            IconButton(onClick = { onBottomSheet() }) {
                Icon(
                    Icons.Default.MoreVert,
                    "Analysis"
                )
            }
        }
    )
}

@Composable
fun ListGrades(grades: List<Grade>, paddingValues: PaddingValues) {
    LazyColumn(
        Modifier
            .padding(paddingValues)
            .fillMaxWidth()
    ) {
        items(grades.size) {
            val grade = grades[it]
            GradeItem(grade.name, grade.qualification, grade.percentage)
        }
    }
}

@Composable
fun GradeItem(name: String, qualification: Double, percentage: Double) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(Padding.normalPadding)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(Padding.normalPadding),
                painter = painterResource(id = R.drawable.article),
                contentDescription = "course"
            )
            Column(
                Modifier
                    .padding(Padding.normalPadding)
                    .fillMaxWidth()
            ) {
                Text(
                    text = name,
                    modifier = Modifier.padding(Padding.smallPadding),
                    fontSize = FontSizes.normal,
                    fontWeight = FontWeight.Bold
                )
                BoxWithConstraints {
                    val localConfig = LocalConfiguration.current
                    val screenWidth = localConfig.screenWidthDp
                    Log.i("Screen", "width: $screenWidth")
                    if (maxWidth > 300.dp) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LabelsGrade(qualification, percentage)
                        }
                    } else {
                        Column {
                            LabelsGrade(qualification, percentage)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelsGrade(qualification: Double, percentage: Double) {
    Row(Modifier.padding(Padding.smallPadding)) {
        Text(text = stringResource(R.string.qualification))
        Text(text = ": $qualification")
    }
    Row(Modifier.padding(Padding.smallPadding)) {
        Text(text = stringResource(R.string.percentage))
        Text(text = ": $percentage")
    }
}
