package com.jarabrama.average.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.model.Grade
import com.jarabrama.average.service.CourseService
import com.jarabrama.average.service.GradeService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpandedCourseViewModel @AssistedInject constructor(
    gradeService: GradeService,
    courseService: CourseService,
    @Assisted courseId: Int
) : ViewModel() {
    private val _course = MutableStateFlow(courseService.get(courseId))
    val course = _course.asStateFlow()

    private val _grades = MutableStateFlow(listOf<Grade>())
    val grades = _grades.asStateFlow()

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