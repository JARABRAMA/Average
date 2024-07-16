package com.jarabrama.average.repository

import com.jarabrama.average.model.Grade

interface GradeRepository {
    fun findAll(): MutableList<Grade>
    fun findAllByCourseId(courseId: Int): List<Grade>
    fun save(grades: MutableList<Grade>): Boolean
    fun delete(grade: Grade)
    fun get(id: Int): Grade?
}