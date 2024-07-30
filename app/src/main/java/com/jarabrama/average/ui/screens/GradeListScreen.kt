package com.jarabrama.average.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.model.Grade
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.GradeListViewModel

@Composable
fun GradeListScreen(
    viewModel: GradeListViewModel,
    paddingValues: PaddingValues
) {
    val grades by viewModel.grades.collectAsState()
    GradeListScreen(grades = grades, paddingValues)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeListScreen(grades: List<Grade>, parentPadding: PaddingValues) {
    val scaffoldPadding = PaddingValues(bottom = parentPadding.calculateBottomPadding())
    Scaffold(
        modifier = Modifier.padding(scaffoldPadding),
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.grades)) }) }
    ) { paddingValues ->
        ListGrades(grades, paddingValues)
    }
}

@Composable
fun ListGrades(grades: List<Grade>, paddingValues: PaddingValues) {
    LazyColumn(
        Modifier
            .padding(paddingValues)
            .fillMaxWidth()) {
        items(grades.size) {
            val grade = grades[it]
            GradeItem(grade.name, grade.qualification, grade.percentage)
        }
    }
}

@Preview
@Composable
private fun GradeItem() {
    GradeItem("Python Course", 5.0, 25.0)
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
