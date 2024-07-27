package com.jarabrama.average.exceptions.gradeExceptions

import com.jarabrama.average.exceptions.courseExceptions.CourseException

class InvalidQualificationException(qualification: Double) :
    CourseException("Qualification $qualification is out of the limits of qualification")