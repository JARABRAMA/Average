package com.jarabrama.average.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.event.Event
import com.jarabrama.average.exceptions.gradeExceptions.InvalidPercentageException
import com.jarabrama.average.exceptions.gradeExceptions.InvalidQualificationException
import com.jarabrama.average.model.Settings
import com.jarabrama.average.repository.SettingsRepository
import com.jarabrama.average.service.CourseService
import com.jarabrama.average.service.GradeService
import com.jarabrama.average.utils.Strings
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.lang.Double.parseDouble


class NewGradeViewModel @AssistedInject constructor(
    private val gradeService: GradeService,
    private val settingsRepository: SettingsRepository,
    private val courseService: CourseService,
    @Assisted private val courseId: Int
) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _qualification = MutableStateFlow("")
    val qualification = _qualification.asStateFlow()

    private val _percentage = MutableStateFlow("")
    val percentage = _percentage.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    private val _errorState = MutableStateFlow(false)

    fun getErrorMessage(): String = _errorMessage.value
    fun getErrorState(): Boolean = _errorState.value

    fun onDismissSnackbar() {
        _errorState.value = !_errorState.value
    }

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onQualificationChange(value: String) {
        _qualification.value = value
    }

    fun onPercentageChange(value: String) {
        _percentage.value = value
    }

    fun onSave() {
        if (_name.value == "") {
            _errorMessage.value = Strings.ERROR_NAME
            _errorState.value = true
        } else {
            try {
                val percentageValue = parseDouble(_percentage.value)
                val qualificationValue = parseDouble(_qualification.value) // NumberFormatException
                gradeService.newGrade(
                    courseId,
                    name.value,
                    qualificationValue,
                    percentageValue
                )
                EventBus.getDefault()
                    .post(
                        Event.GradeAddedEvent(
                            name.value,
                            qualificationValue,
                            percentageValue
                        )
                    )
            } catch (e: NumberFormatException) {
                _errorState.value = true
                _errorMessage.value = Strings.ERROR_DECIMAL
            } catch (e: InvalidPercentageException) {
                _errorState.value = true
                _errorMessage.value = Strings.ERROR_PERCENTAGE
            } catch (e: InvalidQualificationException) {

                _errorState.value = true
                _errorMessage.value = Strings.ERROR_QUALIFICATION
            }
        }
    }
}