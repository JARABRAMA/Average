package com.jarabrama.average

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jarabrama.average.di.ExpandedCourseAssistedFactory
import com.jarabrama.average.di.NewGradeAssistedFactory
import com.jarabrama.average.ui.screens.AverageApp
import com.jarabrama.average.ui.theme.AverageTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var expandedCourseAssistedFactory: ExpandedCourseAssistedFactory

    @Inject
    lateinit var newGradeAssistedFactory: NewGradeAssistedFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AverageTheme {
                AverageApp(expandedCourseAssistedFactory, newGradeAssistedFactory)
            }
        }
    }
}
