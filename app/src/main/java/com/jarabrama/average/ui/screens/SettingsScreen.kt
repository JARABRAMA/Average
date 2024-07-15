package com.jarabrama.average.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.SettingsViewModel


@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel,
    paddingValues: PaddingValues
) {
    val minQualification by viewModel.minQualification.collectAsState()
    val maxQualification by viewModel.maxQualification.collectAsState()
    val goal by viewModel.goal.collectAsState()

    SettingsScreen(
        minQualification = minQualification,
        maxQualification = maxQualification,
        goal = goal,
        onMinQualificationChange = { viewModel.onMinQualificationChange(it) },
        onMaxQualificationChange = { viewModel.onMaxQualificationChange(it) },
        onGoalChange = { viewModel.onGoalChange(it) },
        navController = navController,
        onSave = { viewModel.onSave() },
        paddingValues = paddingValues
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    minQualification: String,
    maxQualification: String,
    goal: String,
    onMinQualificationChange: (String) -> Unit,
    onMaxQualificationChange: (String) -> Unit,
    onGoalChange: (String) -> Unit,
    navController: NavController,
    onSave: () -> Unit,
    paddingValues: PaddingValues
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.setting)) })
        },
        floatingActionButton = {
            SaveFloatingButton(onSave, navController, paddingValues)
        }
    ) { values ->
        SettingsComponents(
            paddingValues = values,
            minQualification = minQualification,
            maxQualification = maxQualification,
            goal = goal,
            onMinQualificationChange = { onMinQualificationChange(it) },
            onMaxQualificationChange = { onMaxQualificationChange(it) },
            onGoalChange = { onGoalChange(it) },
        )
    }
}

@Composable
private fun SaveFloatingButton(
    onSave: () -> Unit,
    navController: NavController,
    paddingValues: PaddingValues
) {
    FloatingActionButton(
        onClick = {
            onSave()
            onBack(navController)
        },
        modifier = Modifier.padding(paddingValues)
    ) {
        Row(
            modifier = Modifier.padding(Padding.smallPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.check),
                contentDescription = "Set",
                modifier = Modifier.padding(Padding.smallPadding)
            )
            Text(
                text = stringResource(R.string.set),
                modifier = Modifier.padding(Padding.smallPadding)
            )
            Spacer(modifier = Modifier.padding(Padding.smallPadding))
        }
    }
}

@Composable
fun SettingsComponents(
    paddingValues: PaddingValues,
    minQualification: String,
    maxQualification: String,
    goal: String,
    onMinQualificationChange: (String) -> Unit,
    onMaxQualificationChange: (String) -> Unit,
    onGoalChange: (String) -> Unit,
) {
    Column(Modifier.padding(paddingValues)) {
        Row {
            CardMinQualification(minQualification) { onMinQualificationChange(it) }
            CardMaxQualification(maxQualification) { onMaxQualificationChange(it) }
        }
        CardGoal(goal) { onGoalChange(it) }
    }
}

@Composable
fun CardGoal(goal: String, onGoalChange: (String) -> Unit) {
    Card(
        Modifier
            .padding(Padding.smallPadding)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(Padding.normalPadding)) {
            Text(text = "Goal", fontSize = FontSizes.normal)
            Text(
                text = "Set the average that you wish to obtain",
                fontSize = FontSizes.small,
                lineHeight = 1.sp
            )
            Spacer(modifier = Modifier.padding(Padding.cardList))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(.4f),
                shape = RoundedCornerShape(16),
                value = goal,
                onValueChange = { onGoalChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun CardMinQualification() {
    CardMinQualification(minQualification = "0.0") {}
}

@Composable
private fun CardMinQualification(
    minQualification: String,
    onMinQualificationChange: (String) -> Unit
) {
    Card(Modifier.padding(Padding.smallPadding)) {
        Column(
            modifier = Modifier
                .padding(Padding.normalPadding)
                .fillMaxWidth(.45f)
        ) {
            Text(text = stringResource(R.string.minimum_qualification), fontSize = FontSizes.normal)

            Text(
                text = stringResource(R.string.label_min_qualification),
                fontSize = FontSizes.small,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.padding(Padding.cardList))
            OutlinedTextField(
                shape = RoundedCornerShape(16),
                value = minQualification,
                onValueChange = { onMinQualificationChange(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                maxLines = 1,
                singleLine = true
            )
        }
    }
}

@Composable
fun CardMaxQualification(maxQualification: String, onMaxQualificationChange: (String) -> Unit) {
    Card(Modifier.padding(Padding.smallPadding)) {
        Column(Modifier.padding(Padding.normalPadding)) {
            Text(text = stringResource(R.string.maximum_qualification), fontSize = FontSizes.normal)
            Text(
                text = stringResource(R.string.maximum_qualification_label),
                fontSize = FontSizes.small,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.padding(Padding.cardList))
            OutlinedTextField(
                shape = RoundedCornerShape(16),
                value = maxQualification,
                onValueChange = { onMaxQualificationChange(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                maxLines = 1,
                singleLine = true
            )
        }
    }
}