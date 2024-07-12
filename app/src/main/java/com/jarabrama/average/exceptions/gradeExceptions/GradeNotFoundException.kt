package com.jarabrama.average.exceptions.gradeExceptions

class GradeNotFoundException(id: Int): GradeException("grade $id not found"){
}