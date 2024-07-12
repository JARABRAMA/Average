package com.jarabrama.average.repository

import com.jarabrama.average.model.Course

interface CourseRepository {
    fun findAll(): List<Course>
    fun get(id: Int): Course?
    fun save(courses: MutableList<Course>): Boolean
    fun delete(id: Int)
}

