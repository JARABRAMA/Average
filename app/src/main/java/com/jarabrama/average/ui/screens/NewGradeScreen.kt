package com.jarabrama.average.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.view.SoftwareKeyboardControllerCompat
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.Screen
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.NewGradeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.Normalizer.Form

@Composable
fun NewGradeScreen(
    viewModel: NewGradeViewModel,
    navController: NavController
) {
    val name by viewModel.name.collectAsState()
    val qualification by viewModel.qualification.collectAsState()
    val percentage by viewModel.percentage.collectAsState()
    val scope: CoroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    NewGradeScreen(
        name,
        qualification,
        percentage,
        viewModel::onNameChange,
        viewModel::onQualificationChange,
        viewModel::onPercentageChange,
        viewModel::onSave,
        navController,
        scope,
        snackbarHostState,
        viewModel::getErrorState,
        viewModel::getErrorMessage,
        viewModel::onDismissSnackbar
    )
}

@Composable
fun NewGradeScreen(
    name: String,
    qualification: String,
    percentage: String,
    onNameChange: (String) -> Unit,
    onQualificationChange: (String) -> Unit,
    onPercentageChange: (String) -> Unit,
    onSave: () -> Unit,
    navController: NavController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    getErrorState: () -> Boolean,
    getErrorMessage: () -> String,
    onDismissSnackbar: () -> Unit
) {
    Scaffold(
        topBar = { NewGradeTopBar(navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Form(
            it,
            name,
            qualification,
            percentage,
            onNameChange,
            onQualificationChange,
            onPercentageChange,
            onSave,
            navController,
            scope,
            snackbarHostState,
            getErrorState,
            getErrorMessage,
            onDismissSnackbar
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NewGradeTopBar(navController: NavController) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.new_grade)) },
        navigationIcon = {
            IconButton(onClick = { onBack(navController) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
        }
    )
}

@Composable
fun Form(
    paddingValues: PaddingValues,
    name: String,
    qualification: String,
    percentage: String,
    onNameChange: (String) -> Unit,
    onQualificationChange: (String) -> Unit,
    onPercentageChange: (String) -> Unit,
    onSave: () -> Unit,
    navController: NavController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    getErrorState: () -> Boolean,
    getErrorMessage: () -> String,
    onDismissSnackbar: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val keyboardController = LocalSoftwareKeyboardController.current
        Column(
            Modifier
                .fillMaxWidth(.8f)
                .padding(paddingValues)
        ) {
            Spacer(Modifier.padding(Padding.normalPadding))
            Text(
                text = stringResource(R.string.grade_details),
                Modifier.fillMaxWidth(),
                fontSize = FontSizes.normal
            )
            Spacer(Modifier.padding(Padding.normalPadding))
            NameForm(name, onNameChange)
            Spacer(Modifier.padding(Padding.normalPadding))
            QualificationForm(qualification, onQualificationChange)
            Spacer(Modifier.padding(Padding.normalPadding))
            PercentageForm(percentage, onPercentageChange)
            Spacer(Modifier.padding(Padding.normalPadding))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = {
                    keyboardController?.hide()
                    onSave()
                    onErrorSnackbar(
                        getErrorState,
                        scope,
                        snackbarHostState,
                        getErrorMessage,
                        onDismissSnackbar,
                        navController
                    )
                }) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }
}


private fun onErrorSnackbar(
    getErrorState: () -> Boolean,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    getErrorMessage: () -> String,
    onDismissSnackbar: () -> Unit,
    navController: NavController
) {
    if (getErrorState()) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = getErrorMessage(),
                duration = SnackbarDuration.Short
            )
        }
        onDismissSnackbar()
    } else {
        onBack(navController)
    }
}

@Composable
private fun PercentageForm(
    percentage: String,
    onPercentageChange: (String) -> Unit
) {
    Text(text = stringResource(R.string.percentage))
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = percentage,
        onValueChange = { onPercentageChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        maxLines = 1,
        placeholder = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = stringResource(id = R.string.percentage))
                Icon(painterResource(id = R.drawable.percent), "Percentage")
            }
        }
    )
}

@Composable
private fun QualificationForm(
    qualification: String,
    onQualificationChange: (String) -> Unit
) {
    Text(text = stringResource(R.string.qualification))
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = qualification,
        onValueChange = { onQualificationChange(it) },
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        placeholder = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = stringResource(R.string.qualification))
                Icon(painterResource(id = R.drawable.tag), "Qualification")
            }
        }
    )
}

@Composable
private fun NameForm(name: String, onNameChange: (String) -> Unit) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = name,
        onValueChange = { onNameChange(it) },
        singleLine = true,
        maxLines = 1,
        placeholder = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = stringResource(R.string.grade_name))
                Icon(Icons.Filled.Edit, "GradeName")
            }
        }
    )
}