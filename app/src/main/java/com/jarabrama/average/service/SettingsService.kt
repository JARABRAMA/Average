package com.jarabrama.average.service

import com.jarabrama.average.model.Settings

interface SettingsService {
    fun getSettings(): Settings
    fun setSettings(settings: Settings): Settings
}