package com.jarabrama.average.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jarabrama.average.event.Event
import com.jarabrama.average.service.CourseService
import com.jarabrama.average.utils.Strings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _errorMessage = MutableStateFlow("")


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
        _errorMessage.value = ""
    }

    fun onSave() {
        if (name.value == "") {
            _errorMessage.value = Strings.ERROR_NAME
            _errorState.value = true
            Log.e("New course view model", _errorMessage.value)
        } else {
            toTitleCase()
            try {
                val creditsValue: Int = parseInt(_credits.value)
                courseService.newCourse(_name.value, creditsValue)
                EventBus.getDefault().post(Event.CourseAddedEvent(name.value, creditsValue))
            } catch (e: NumberFormatException) {
                _errorMessage.value = Strings.ERROR_CREDITS
                _errorState.value = true

                Log.e("New course view model", _errorMessage.value)
            }
        }
    }

    fun getErrorState(): Boolean = _errorState.value
    fun getErrorMessage(): String = _errorMessage.value


    private fun toTitleCase() {
        _name.value.replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase()
            } else {
                it.toString()
            }
        }
    }
}