package com.jarabrama.average.exceptions.settingExceptions

class InvalidMinQualificationException(minQualification: Double, maxQualification: Double) :
    SettingException("Error: minimum qualification: $minQualification is over maximum  qualification $maxQualification") {
}