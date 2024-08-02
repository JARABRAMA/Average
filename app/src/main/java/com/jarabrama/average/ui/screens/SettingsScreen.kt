package com.jarabrama.average.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val minQualification by viewModel.minQualification.collectAsState()
    val maxQualification by viewModel.maxQualification.collectAsState()
    val goal by viewModel.goal.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    SettingsScreen(
        minQualification = minQualification,
        maxQualification = maxQualification,
        goal = goal,
        snackbarHostState = snackbarHostState,
        scope = scope,
        onMinQualificationChange = { viewModel.onMinQualificationChange(it) },
        onMaxQualificationChange = { viewModel.onMaxQualificationChange(it) },
        onGoalChange = { viewModel.onGoalChange(it) },
        navController = navController,
        onSave = { viewModel.onSave() },
        getErrorState = { viewModel.geErrorState() },
        onDismiss = { viewModel.onDismiss() },
        getErrorMessage = { viewModel.getErrorMessage() }
    )

}

@Composable
fun SettingsScreen(
    minQualification: String,
    maxQualification: String,
    goal: String,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onMinQualificationChange: (String) -> Unit,
    onMaxQualificationChange: (String) -> Unit,
    onGoalChange: (String) -> Unit,
    navController: NavController,
    onSave: () -> Unit,
    getErrorState: () -> Boolean,
    onDismiss: () -> Unit,
    getErrorMessage: () -> String
) {
    Scaffold(
        topBar = {
            SettingsTopBar()
        },
        floatingActionButton = {
            SaveFloatingButton(
                onSave = onSave,
                navController = navController,
                getErrorState = getErrorState,
                getErrorMessage = getErrorMessage,
                onDismiss = onDismiss,
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { values ->
        SettingsComponents(
            parentPadding = values,
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
@OptIn(ExperimentalMaterial3Api::class)
private fun SettingsTopBar() {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = R.string.setting)) },
    )
}

@Composable
private fun SaveFloatingButton(
    onSave: () -> Unit,
    navController: NavController,
    getErrorState: () -> Boolean,
    getErrorMessage: () -> String,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    ExtendedFloatingActionButton(
        onClick = {
            onSaveSettings(
                onSave,
                getErrorState,
                getErrorMessage,
                scope,
                snackbarHostState,
                onDismiss,
                navController
            )
        },
        text = { Text(text = stringResource(id = R.string.save)) },
        icon = { Icon(Icons.Default.Check, "Save") }
    )
}

private fun onSaveSettings(
    onSave: () -> Unit,
    getErrorState: () -> Boolean,
    getErrorMessage: () -> String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
    navController: NavController
) {
    onSave()
    val errorState = getErrorState()
    if (errorState) {
        val errorMessage = getErrorMessage()
        showSanckbar(scope, snackbarHostState, errorMessage, onDismiss)
    } else {
        onBack(navController)
    }
}


private fun showSanckbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    errorMessage: String,
    onDismiss: () ->  Unit
) {
    scope.launch {
        snackbarHostState.showSnackbar(
            message = errorMessage,
            duration = SnackbarDuration.Short,
            withDismissAction = true
        )
        onDismiss()
    }
}

@Composable
fun SettingsComponents(
    parentPadding: PaddingValues,
    minQualification: String,
    maxQualification: String,
    goal: String,
    onMinQualificationChange: (String) -> Unit,
    onMaxQualificationChange: (String) -> Unit,
    onGoalChange: (String) -> Unit,
) {

    Column(Modifier.padding(parentPadding)) {
        Column(Modifier.padding(Padding.cardList)) {
            Row {
                CardMinQualification(minQualification) { onMinQualificationChange(it) }
                CardMaxQualification(maxQualification) { onMaxQualificationChange(it) }
            }
            CardGoal(goal) { onGoalChange(it) }
        }
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
            Text(
                text = stringResource(R.string.goal),
                fontSize = FontSizes.normal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.goal_label),
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
            Text(
                text = stringResource(R.string.minimum_qualification),
                fontSize = FontSizes.normal,
                fontWeight = FontWeight.Bold
            )

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
            Text(
                text = stringResource(R.string.maximum_qualification),
                fontSize = FontSizes.normal,
                fontWeight = FontWeight.Bold
            )
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