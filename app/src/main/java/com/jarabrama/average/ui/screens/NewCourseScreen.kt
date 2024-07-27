package com.jarabrama.average.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavController
import com.jarabrama.average.R
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.NewCourseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NewCourseScreen(viewModel: NewCourseViewModel, navController: NavController) {
    val credits by viewModel.credits.collectAsState()
    val name by viewModel.name.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    NewCourseScreen(
        scope = scope,
        snackBarHostState = snackBarHostState,
        getErrorState = { viewModel.getErrorState() },
        getErrorMessage = { viewModel.getErrorMessage() },
        credits = credits,
        name = name,
        onNameChange = { viewModel.onNameChange(it) },
        onCreditChange = { viewModel.onCreditsChange(it) },
        onSave = { viewModel.onSave() },
        onDismissError = { viewModel.onDismissError() },
        navController = navController
    )
}

@Composable
private fun NewCourseScreen(
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    getErrorState: () -> Boolean,
    getErrorMessage: () -> String,
    credits: String,
    name: String,
    onNameChange: (String) -> Unit,
    onCreditChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismissError: () -> Unit,
    navController: NavController
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = { NewCourseTopBar(title = "New Course", navController = navController) }
    ) {
        FormNewCourse(
            scope = scope,
            snackBarHostState = snackBarHostState,
            navController = navController,
            padding = it,
            getErrorState = { getErrorState() },
            getErrorMessage = { getErrorMessage() },
            credits = credits,
            name = name,
            onNameChange = onNameChange,
            onCreditChange = onCreditChange,
            onSave = onSave,
            onDismissError = { onDismissError() },
            namePlaceholder = { NamePlaceholder() },
            creditsPlaceholder = { CreditsPlaceholder() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCourseTopBar(title: String, navController: NavController) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { onBack(navController) }) {
                Icon(painter = painterResource(id = R.drawable.back), contentDescription = "back")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackButton(onBack: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        onClick = { onBack() }
    ) {
        Icon(painterResource(id = R.drawable.back), null)
    }
}

@Composable
fun FormNewCourse(
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    navController: NavController,
    padding: PaddingValues,
    getErrorState: () -> Boolean,
    getErrorMessage: () -> String,
    credits: String,
    name: String,
    onNameChange: (String) -> Unit,
    onCreditChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismissError: () -> Unit,
    namePlaceholder: @Composable () -> Unit,
    creditsPlaceholder: @Composable () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        Modifier
            .padding(padding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(Padding.normalPadding))
        Text(
            text = stringResource(R.string.course_details),
            modifier = Modifier.fillMaxWidth(0.8f),
            fontSize = FontSizes.normal
        )
        Spacer(Modifier.padding(Padding.normalPadding))
        NameForm(name, onNameChange, namePlaceholder)
        Spacer(modifier = Modifier.padding(Padding.smallPadding))
        CourseForm(credits, onCreditChange, creditsPlaceholder)
        Spacer(modifier = Modifier.padding(Padding.smallPadding))
        Row(Modifier.fillMaxWidth(.8f), horizontalArrangement = Arrangement.End) {
            Button({
                    keyboardController?.hide()
                    onSaveClick(
                        onSave,
                        { getErrorMessage() },
                        scope,
                        snackBarHostState,
                        { getErrorState() },
                        navController,
                        onDismissError
                    )
                }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.save), fontSize = FontSizes.normal)
                }
            }

        }
    }
}

private fun onSaveClick(
    onSave: () -> Unit,
    getErrorMessage: () -> String,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    getErrorState: () -> Boolean,
    navController: NavController,
    onDismissError: () -> Unit
) {
    onSave()
    val errorState = getErrorState()
    if (errorState) {
        val errorMessage = getErrorMessage()
        Log.e("Error", errorMessage)
        scope.launch {
            snackBarHostState.showSnackbar(
                errorMessage, "Dismiss", true, SnackbarDuration.Short
            )
        }
        onDismissError()
    } else {
        onBack(navController)
        Log.i("New Course", "New course has been added")
    }
}


@Composable
private fun CourseForm(
    credits: String,
    onCreditChange: (String) -> Unit,
    coursePlaceholder: @Composable () -> Unit
) {
    Text(text = stringResource(R.string.credits), modifier = Modifier.fillMaxWidth(.8f))
    TextField(
        value = credits,
        onValueChange = { onCreditChange(it) },
        modifier = Modifier.fillMaxWidth(.8f),
        singleLine = true,
        maxLines = 1,
        placeholder = coursePlaceholder,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    )
}

@Composable
private fun NameForm(
    name: String,
    onNameChange: (String) -> Unit,
    namePlaceholder: @Composable () -> Unit
) {
    Text(text = stringResource(R.string.name), modifier = Modifier.fillMaxWidth(.8f))
    TextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth(0.8f),
        singleLine = true,
        maxLines = 1,
        placeholder = namePlaceholder
    )
}

@Composable
fun NamePlaceholder() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = stringResource(R.string.course_name))
        Icon(painterResource(id = R.drawable.pen), null)
    }
}

@Composable
fun CreditsPlaceholder() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = stringResource(R.string.credits_placeholder))
        Icon(painterResource(id = R.drawable.database), null)
    }
}