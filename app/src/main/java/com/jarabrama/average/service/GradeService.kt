package com.jarabrama.average.service

import com.jarabrama.average.model.Grade

interface GradeService {
    fun findAll(courseId: Int): List<Grade>
    fun newGrade(courseId: Int, name: String, qualification: Double, percentage: Double): Grade
    fun update(grade: Grade): Grade
    fun delete(id: Int)
    fun get(id: Int): Grade
    fun getAverage(courseId: Int): Double
}