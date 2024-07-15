package com.jarabrama.average.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarabrama.average.model.Settings
import com.jarabrama.average.repository.SettingsRepository
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
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private lateinit var settings: Settings

    private val _maxQualification = MutableStateFlow("")
    val maxQualification = _maxQualification.asStateFlow()

    private val _minQualification = MutableStateFlow("")
    val minQualification = _minQualification.asStateFlow()

    private val _goal = MutableStateFlow("")
    val goal = _goal.asStateFlow()

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
            val settings = settingsRepository.getSettings()
            withContext(Dispatchers.Main) {
                _maxQualification.value = settings.maxQualification.toString()
                _minQualification.value = settings.minQualification.toString()
                _goal.value = settings.goal.toString()
            }
        }
    }

    fun setValues() {
        try {
            settings = Settings(
                maxQualification = parseDouble(_maxQualification.value),
                minQualification = parseDouble(_minQualification.value),
                goal = parseDouble(_goal.value)
            )
            viewModelScope.launch(Dispatchers.IO) {
                settingsRepository.setSettings(settings = settings)
            }
        } catch (e: NumberFormatException) {
            TODO("manage this exception with a snackbar")
        }
    }

    fun onSave() {
        TODO("Not yet implemented")
    }
}