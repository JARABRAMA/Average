package com.jarabrama.average.exceptions.gradeExceptions

import com.jarabrama.average.model.Grade

class SavingGradeException(grade: Grade): GradeException("Can't save $grade") {
}