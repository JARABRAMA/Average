package com.jarabrama.average.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.model.Grade
import com.jarabrama.average.service.GradeService
import com.jarabrama.average.utils.Functions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GradeListViewModel @Inject constructor(private val gradeService: GradeService) : ViewModel() {

    private val _grades = MutableStateFlow(listOf<Grade>())
    val grades = _grades.asStateFlow()

    private val _average = MutableStateFlow("")
    val average = _average.asStateFlow()

    init {
        updateGrades()
    }

    private fun updateGrades() {
        viewModelScope.launch(Dispatchers.IO) {
            val grades = gradeService.findAll()
            val average = Functions.formatDecimal(gradeService.getSimpleAverage())
            withContext(Dispatchers.Main) {
                _grades.value = grades
                _average.value = average
            }
        }
    }
}