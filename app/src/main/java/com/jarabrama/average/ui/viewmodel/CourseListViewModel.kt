package com.jarabrama.average.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.event.Event
import com.jarabrama.average.model.Course
import com.jarabrama.average.service.CourseService
import com.jarabrama.average.service.SettingsService
import com.jarabrama.average.utils.Functions
import com.jarabrama.average.utils.Strings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Integer.parseInt
import java.lang.NumberFormatException
import javax.inject.Inject

@HiltViewModel
class CourseListViewModel @Inject constructor(
    private val courseService: CourseService,
    private val settingsService: SettingsService
) :
    ViewModel() {

    private val _courses = MutableStateFlow(listOf<Course>())
    val courses = _courses.asStateFlow()

    private val _currentAverages = MutableStateFlow(mapOf<Int, String>())
    val currentAverages = _currentAverages.asStateFlow()

    private val _analysis = MutableStateFlow("")
    val analysis = _analysis.asStateFlow()

    private val _currentCreditAverage = MutableStateFlow("")
    val currentCreditAverage = _currentCreditAverage.asStateFlow()

    private val _editName = MutableStateFlow("")
    val editName = _editName.asStateFlow()

    private val _editCredits = MutableStateFlow("")
    val editCredits = _editCredits.asStateFlow()

    private val showSnackbar = MutableStateFlow(false)
    private val snackbarMessage = MutableStateFlow("")

    val setValues = { name: String, credits: String ->
        _editName.value = name
        _editCredits.value = credits
    }

    val onEditNameChange = { name: String -> _editName.value = name }
    val onEditCreditChange = { credits: String -> _editCredits.value = credits }

    val getSnackbarStatus = { showSnackbar.value }
    val getSnackbarMessage = { snackbarMessage.value }

    val onDismissSnackbar = { showSnackbar.value = false }

    val onEditCourse = { courseId: Int ->
        if (_editName.value.isEmpty()) {
            snackbarMessage.value = Strings.ERROR_NAME
            showSnackbar.value = true
        } else {
            viewModelScope.launch {
                try {
                    courseService.update(
                        Course(
                            courseId,
                            _editName.value,
                            parseInt(_editCredits.value)
                        )
                    )
                    updateCourses()
                } catch (e: NumberFormatException) {
                    showSnackbar.value = true
                    snackbarMessage.value = Strings.ERROR_CREDITS
                }
            }
        }
    }

    init {
        EventBus.getDefault().register(this)
        updateCourses()
    }

    @Subscribe
    fun courseAdded(event: Event.CourseAddedEvent) {
        updateCourses()
    }


    fun onDeleteCourse(courseId: Int) {
        courseService.delete(courseId)
        updateCourses()
    }

    private fun updateCourses() {
        viewModelScope.launch(Dispatchers.IO) {
            val courses = courseService.findAll()

            val settings = settingsService.getSettings()
            val analysis =
                courseService.getAnalysis(settings.maxQualification, settings.minQualification)
            val creditAverage = Functions.formatDecimal(courseService.getCreditAverage())
            val averages = courseService.getAverages()
            withContext(Dispatchers.Main) {
                _analysis.value = analysis
                _currentCreditAverage.value = creditAverage
                _courses.value = courses
                _currentAverages.value = averages
            }
        }
    }
}