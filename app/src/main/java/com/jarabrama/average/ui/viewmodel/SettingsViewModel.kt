package com.jarabrama.average.ui.viewmodel

import androidx.compose.runtime.structuralEqualityPolicy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.exceptions.settingExceptions.SettingException
import com.jarabrama.average.model.Settings
import com.jarabrama.average.service.SettingsService
import com.jarabrama.average.utils.Functions
import com.jarabrama.average.utils.Strings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Double.parseDouble
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsService: SettingsService
) : ViewModel() {
    private lateinit var settings: Settings

    private val _maxQualification = MutableStateFlow("")
    val maxQualification = _maxQualification.asStateFlow()

    private val _minQualification = MutableStateFlow("")
    val minQualification = _minQualification.asStateFlow()

    private val _goal = MutableStateFlow("")
    val goal = _goal.asStateFlow()

    private val _errorState = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow("")

    init {
        getValues()
    }

    fun onMinQualificationChange(value: String) {
        _minQualification.value = value
    }

    fun onMaxQualificationChange(value: String) {
        _maxQualification.value = value
    }

    fun onGoalChange(value: String) {
        _goal.value = value
    }

    private fun getValues() {
        viewModelScope.launch(Dispatchers.IO) {
            val settings = settingsService.getSettings()
            withContext(Dispatchers.Main) {
                _maxQualification.value = Functions.formatDecimal(settings.maxQualification)
                _minQualification.value = Functions.formatDecimal(settings.minQualification)
                _goal.value = Functions.formatDecimal(settings.goal)
            }
        }
    }

    fun onSave() {
        try {
            settings = Settings(
                maxQualification = parseDouble(_maxQualification.value),
                minQualification = parseDouble(_minQualification.value),
                goal = parseDouble(_goal.value)
            )
            settingsService.setSettings(settings = settings)
        } catch (e: NumberFormatException) {
            _errorState.value = true
            _errorMessage.value = Strings.ERROR_SETTINGS_TYPE
        } catch (e: SettingException) {
            _errorState.value = true
            _errorMessage.value = e.message ?: Strings.ERROR
        }
    }

    fun onDismiss() {
        _errorState.value = false
    }
    fun geErrorState(): Boolean = _errorState.value
    fun getErrorMessage(): String = _errorMessage.value
}