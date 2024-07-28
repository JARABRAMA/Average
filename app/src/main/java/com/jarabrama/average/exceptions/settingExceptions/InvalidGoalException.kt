package com.jarabrama.average.exceptions.settingExceptions

class InvalidGoalException(goal: Double) :
    SettingException("Error: Goal: $goal is below of minimum qualification or above maximum qualification") {
}