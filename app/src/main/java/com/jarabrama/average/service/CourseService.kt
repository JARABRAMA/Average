package com.jarabrama.average.service

import com.jarabrama.average.model.Course

interface CourseService {
    fun findAll(): List<Course>
    fun newCourse(name: String, credits: Int): Course
    fun update(course: Course): Course
    fun delete(id: Int)
    fun get(id: Int): Course
    fun getAverages(): Map<Int, String>
    fun getAnalysis(maxQualification: Double, minQualification: Double): String
    fun getCreditAverage(): Double
}