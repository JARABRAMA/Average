package com.jarabrama.average.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.event.Event
import com.jarabrama.average.service.CourseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.Integer.parseInt
import javax.inject.Inject

@HiltViewModel
class NewCourseViewModel @Inject constructor(
    private val courseService: CourseService
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _errorState = MutableStateFlow(false)
    val errorState = _errorState.asStateFlow()


    private val _credits = MutableStateFlow("")
    val credits = _credits.asStateFlow()

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onCreditsChange(credits: String) {
        _credits.value = credits
    }
    fun onDismissError() {
        _errorState.value = false
    }

    fun onSave() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val creditsValue: Int = parseInt(_credits.value)
                courseService.newCourse(_name.value, creditsValue)
                EventBus.getDefault().post(Event.CourseAddedEvent(name.value, creditsValue))
            } catch (e: NumberFormatException) {
                _errorState.value = true
            }
        }
    }


}