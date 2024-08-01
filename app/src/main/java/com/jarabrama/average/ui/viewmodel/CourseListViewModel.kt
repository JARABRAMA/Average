package com.jarabrama.average.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.event.Event
import com.jarabrama.average.model.Course
import com.jarabrama.average.service.CourseService
import com.jarabrama.average.service.SettingsService
import com.jarabrama.average.utils.Functions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@HiltViewModel
class CourseListViewModel @Inject constructor(private val courseService: CourseService, private val settingsService: SettingsService) :
    ViewModel() {

    private val _courses = MutableStateFlow(listOf<Course>())
    val courses = _courses.asStateFlow()

    private val _currentAverages = MutableStateFlow(mapOf<Int, String>())
    val currentAverages = _currentAverages.asStateFlow()

    private val _analysis = MutableStateFlow("")
    val analysis = _analysis.asStateFlow()

    private val _currentCreditAverage = MutableStateFlow("")
    val currentCreditAverage = _currentCreditAverage.asStateFlow()

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
            val analysis = courseService.getAnalysis(settings.maxQualification, settings.minQualification)
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