package com.jarabrama.average.service

import com.jarabrama.average.model.Grade

interface GradeService {
    fun findAll(): List<Grade>
    fun findAllByCourseId(courseId: Int): List<Grade>
    fun newGrade(courseId: Int, name: String, qualification: Double, percentage: Double): Grade
    fun update(grade: Grade): Grade
    fun delete(id: Int)
    fun get(id: Int): Grade
    fun getAverage(courseId: Int): Double
    fun getAnalysis(courseId: Int ): String
    fun getExpectedAverage(courseId: Int): Double
    fun getAvailablePercentage(courseId: Int): Double
    fun getSimpleAverage(): Double
}