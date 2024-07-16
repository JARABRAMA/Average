package com.jarabrama.average.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.model.Grade
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.GradeListViewModel

@Composable
fun GradeListScreen(
    navController: NavController,
    viewModel: GradeListViewModel,
    paddingValues: PaddingValues
) {
    val grades by viewModel.grades.collectAsState()
    GradeListScreen(grades = grades)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeListScreen(grades: List<Grade>) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.grades)) }) }
    ) { paddingValues ->
        ListGrades(grades, paddingValues)
    }
}

@Composable
fun ListGrades(grades: List<Grade>, paddingValues: PaddingValues) {
    Column(Modifier.padding(paddingValues)) {
        grades.forEach {
            GradeItem(it.name, it.qualification, it.percentage)
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
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(Padding.smallPadding)) {
                        Text(text = "Qualification")
                        Text(text = ": $qualification")
                    }
                    Row(Modifier.padding(Padding.smallPadding)) {
                        Text(text = "Percentage")
                        Text(text = ": $percentage")
                    }
                }

            }
        }
    }
}