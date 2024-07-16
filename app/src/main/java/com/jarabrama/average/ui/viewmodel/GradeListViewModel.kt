package com.jarabrama.average.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.model.Grade
import com.jarabrama.average.service.GradeService
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

    init {
        updateGrades()
    }

    private fun updateGrades() {
        viewModelScope.launch(Dispatchers.IO) {
            val grades = gradeService.findAll()
            withContext(Dispatchers.Main) {
                _grades.value = grades
            }
        }
    }
}