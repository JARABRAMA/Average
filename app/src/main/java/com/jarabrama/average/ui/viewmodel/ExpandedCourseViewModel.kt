package com.jarabrama.average.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.event.Event
import com.jarabrama.average.exceptions.courseExceptions.CourseNotFoundException
import com.jarabrama.average.model.Grade
import com.jarabrama.average.service.CourseService
import com.jarabrama.average.service.GradeService
import com.jarabrama.average.utils.Functions
import com.jarabrama.average.utils.Strings
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Integer.parseInt

class ExpandedCourseViewModel @AssistedInject constructor(
    private val gradeService: GradeService,
    private val courseService: CourseService,
    @Assisted private val courseId: Int
) : ViewModel() {
    private val _course = MutableStateFlow(courseService.get(courseId))
    val course = _course.asStateFlow()

    private val _courseName = MutableStateFlow(course.value.name)
    val courseName = _courseName.asStateFlow()

    private val _grades = MutableStateFlow(listOf<Grade>())
    val grades = _grades.asStateFlow()

    private val _editNameValue = MutableStateFlow(_course.value.name)
    val editNameValue = _editNameValue.asStateFlow()

    private val _editCreditValue = MutableStateFlow(_course.value.credits.toString())
    val editCreditValue = _editCreditValue.asStateFlow()

    private val _average = MutableStateFlow("")
    val average = _average.asStateFlow()

    fun getBottomSheetContent(): String {
       return gradeService.getAnalysis(courseId)
    }

    private val _showSnackbar = MutableStateFlow(false)

    private val _errorMessage = MutableStateFlow("")

    init {
        EventBus.getDefault().register(this)
        updateGrades()
    }

    @Subscribe
    fun onGradeAdded(event: Event.GradeAddedEvent) {
        updateGrades()
    }

    private fun updateGrades() {
        viewModelScope.launch(Dispatchers.IO) {
            _grades.value = gradeService.findAllByCourseId(courseId)
            _average.value =
                Functions.formatDecimal(gradeService.getAverage(courseId))
        }
    }

    fun onNameChange(value: String) {
        _editNameValue.value = value
    }

    fun onCreditChange(value: String) {
        _editCreditValue.value = value
    }

    fun onDismissSnackbar() {
        _showSnackbar.value = false
        _errorMessage.value = ""
    }

    fun getErrorState(): Boolean = _showSnackbar.value
    fun getErrorMessage(): String = _errorMessage.value

    fun onUpdate() {
        if (editNameValue.value == "") {
            _showSnackbar.value = true
            _errorMessage.value = Strings.ERROR_NAME
        } else {
            try {
                val updatedCourse = course.value.copy(
                    name = editNameValue.value,
                    credits = parseInt(editCreditValue.value)
                )
                viewModelScope.launch(Dispatchers.IO) {
                    courseService.update(updatedCourse)
                }
                _course.value = updatedCourse
                _courseName.value = updatedCourse.name
                EventBus.getDefault().post(
                    Event.CourseAddedEvent(_editNameValue.value, parseInt(_editCreditValue.value))
                )
            } catch (e: NumberFormatException) {
                Log.e("NumberFormatException", e.message, e)
                _errorMessage.value = Strings.ERROR_CREDITS
                _showSnackbar.value = true
            } catch (e: CourseNotFoundException) {
                Log.e("CourseNotFoundException", e.message, e)
                _errorMessage.value = Strings.ENTITY_NOT_FOUND
                _showSnackbar.value = true
            }
        }
    }

    init {
        updateGrades(gradeService, courseId)
    }

    private fun updateGrades(
        gradeService: GradeService,
        courseId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val grades = gradeService.findAllByCourseId(courseId)
            withContext(Dispatchers.Main) {
                _grades.value = grades
            }
        }
    }

}