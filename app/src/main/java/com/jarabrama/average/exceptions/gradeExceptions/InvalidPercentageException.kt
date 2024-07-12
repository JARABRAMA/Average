package com.jarabrama.average.exceptions.gradeExceptions

class InvalidPercentageException(restingPercentage: Double, inputPercentage: Double) :
    GradeException("input percentage: $inputPercentage beats the resting percentage: $restingPercentage")