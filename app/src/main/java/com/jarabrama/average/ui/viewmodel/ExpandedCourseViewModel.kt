package com.jarabrama.average.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.event.Event
import com.jarabrama.average.exceptions.courseExceptions.CourseNotFoundException
import com.jarabrama.average.exceptions.gradeExceptions.GradeException
import com.jarabrama.average.model.Course
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
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt
import java.lang.NumberFormatException

class ExpandedCourseViewModel @AssistedInject constructor(
    private val gradeService: GradeService,
    private val courseService: CourseService,
    @Assisted private val courseId: Int
) : ViewModel() {
    private val _course = MutableStateFlow<Course?>(null)
    val course = _course.asStateFlow()

    private val _courseName = MutableStateFlow("")
    val courseName = _courseName.asStateFlow()

    private val _editGradeName = MutableStateFlow("")
    val editGradeName = _editGradeName.asStateFlow()

    private val _percentage = MutableStateFlow("")
    val percentage = _percentage.asStateFlow()

    private val _qualification = MutableStateFlow("")
    val qualification = _qualification.asStateFlow()

    val setEditGradeValues = { name: String, qualification: String, percentage: String ->
        _editGradeName.value = name
        _qualification.value = qualification
        _percentage.value = percentage
    }

    val onEditNameChange = { nameValue: String ->
        _editGradeName.value = nameValue
    }

    val onPercentageChange = { percentageValue: String ->
        _percentage.value = percentageValue
    }

    val onQualificationChange = { qualificationValue: String ->
        _qualification.value = qualificationValue
    }

    private val _grades = MutableStateFlow(listOf<Grade>())
    val grades = _grades.asStateFlow()

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
            _course.value = courseService.get(courseId)
            _courseName.value = _course.value?.name ?: ""
            _average.value =
                Functions.formatDecimal(gradeService.getAverage(courseId))
        }
    }

    fun onDismissSnackbar() {
        _showSnackbar.value = false
        _errorMessage.value = ""
    }

    val onDelete = { id: Int ->
        viewModelScope.launch(Dispatchers.IO) {
            gradeService.delete(id)
            updateGrades()
        }
    }

    val onEdit = { id: Int ->
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val qualification = parseDouble(_qualification.value)
                val percentage = parseDouble(_percentage.value)

                gradeService.update(
                    Grade(
                        id,
                        courseId,
                        _editGradeName.value,
                        qualification,
                        percentage
                    )
                )
                updateGrades()
            } catch (e: NumberFormatException) {
                _showSnackbar.value = true
                _errorMessage.value = Strings.ERROR_DECIMAL
            } catch (e: GradeException) {
                _showSnackbar.value = true
                e.message?.let { message -> _errorMessage.value = message }
            }
        }
    }

    fun getErrorState(): Boolean = _showSnackbar.value
    fun getErrorMessage(): String = _errorMessage.value


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