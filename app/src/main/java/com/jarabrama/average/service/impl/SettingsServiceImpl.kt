package com.jarabrama.average.service.impl

import com.jarabrama.average.exceptions.settingExceptions.InvalidGoalException
import com.jarabrama.average.exceptions.settingExceptions.InvalidMinQualificationException
import com.jarabrama.average.exceptions.settingExceptions.SettingException
import com.jarabrama.average.model.Settings
import com.jarabrama.average.repository.SettingsRepository
import com.jarabrama.average.service.SettingsService

class SettingsServiceImpl(private val settingsRepository: SettingsRepository): SettingsService {
    override fun getSettings(): Settings {
        return settingsRepository.getSettings()
    }

    override fun setSettings(settings: Settings): Settings {
        if (settings.minQualification > settings.maxQualification) {
            throw InvalidMinQualificationException(settings.minQualification, settings.maxQualification)
        }
        if (settings.minQualification == settings.maxQualification) {
            throw SettingException("Error: minimum and maximum qualification are equals")
        }
        if (settings.goal !in settings.minQualification..settings.maxQualification) {
            throw InvalidGoalException(settings.goal)
        }
        return settingsRepository.setSettings(settings)
    }
}