package com.jarabrama.average.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jarabrama.average.R
import com.jarabrama.average.ui.theme.ui.FontSizes
import com.jarabrama.average.ui.theme.ui.Padding
import com.jarabrama.average.ui.viewmodel.NewCourseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow

fun onBack(navController: NavController) {
    navController.popBackStack()
}

@Composable
fun NewCourseScreen(viewModel: NewCourseViewModel, navController: NavController) {
    val credits by viewModel.credits.collectAsState()
    val name by viewModel.name.collectAsState()

    NewCourseScreen(
        credits = credits,
        name = name,
        onNameChange = { viewModel.onNameChange(it) },
        onCreditChange = { viewModel.onCreditsChange(it) },
        onSave = { viewModel.onSave() },
        navController = navController
    )
}

@Composable
private fun NewCourseScreen(
    credits: String,
    name: String,
    onNameChange: (String) -> Unit,
    onCreditChange: (String) -> Unit,
    onSave: () -> Unit,
    navController: NavController
) {
    Scaffold(
        topBar = { NewCourseTopBar(title = "New Course", navController = navController) }
    ) {
        FormNewCourse(
            navController,
            it,
            credits,
            name,
            onNameChange,
            onCreditChange,
            onSave,
            { NamePlaceholder() }
        ) { CreditsPlaceholder() }
    }
}

@Composable
fun NewCourseTopBar(title: String, navController: NavController) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RectangleShape
    ) {
        Row(Modifier.systemBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            BackButton { onBack(navController = navController) }
            Spacer(modifier = Modifier.padding(Padding.smallPadding))
            Text(text = title, fontSize = FontSizes.normal)
        }
    }
}

@Preview
@Composable
private fun TopBarPreview() {
    NewCourseTopBar(title = "New Course", navController = rememberNavController())
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

@Preview(showSystemUi = true)
@Composable
private fun FormNewCoursePreview() {
    FormNewCourse(
        navController = rememberNavController(),
        padding = Padding.smallPadding,
        credits = "",
        name = "",
        onNameChange = {},
        onCreditChange = {},
        onSave = {},
        namePlaceholder = { NamePlaceholder() }
    ) { CreditsPlaceholder() }
}

@Composable
fun FormNewCourse(
    navController: NavController,
    padding: PaddingValues,
    credits: String,
    name: String,
    onNameChange: (String) -> Unit,
    onCreditChange: (String) -> Unit,
    onSave: () -> Unit,
    namePlaceholder: @Composable () -> Unit,
    coursePlaceholder: @Composable () -> Unit
) {
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
        CourseForm(credits, onCreditChange, coursePlaceholder)
        Spacer(modifier = Modifier.padding(Padding.smallPadding))
        Row(Modifier.fillMaxWidth(.8f), horizontalArrangement = Arrangement.End) {
            Button(onClick = {
                onSave()
                onBack(navController)
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.save), fontSize = FontSizes.normal)
                }
            }

        }
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