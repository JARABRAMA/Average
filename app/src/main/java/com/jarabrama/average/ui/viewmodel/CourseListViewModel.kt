package com.jarabrama.average.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.event.Event
import com.jarabrama.average.model.Course
import com.jarabrama.average.service.CourseService
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
class CourseListViewModel @Inject constructor(private val courseService: CourseService) :
    ViewModel() {

    private val _courses = MutableStateFlow(listOf<Course>())
    val courses = _courses.asStateFlow()


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
    }

    private fun updateCourses() {
        viewModelScope.launch(Dispatchers.IO) {
            val courses = courseService.findAll()
            withContext(Dispatchers.Main) {
                _courses.value = courses
            }
        }

    }


    fun onEditCourse(courseId: Int) {
        TODO("Not yet implemented")
    }
}