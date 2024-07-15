package com.jarabrama.average.repository

import com.jarabrama.average.model.Settings

interface SettingsRepository {
    fun getSettings(): Settings
    fun setSettings(settings: Settings): Settings
}