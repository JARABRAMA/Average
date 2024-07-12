package com.jarabrama.average.repository.impl

import android.content.Context
import android.util.Log

import com.jarabrama.average.model.Course

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Integer.parseInt
import com.jarabrama.average.repository.CourseRepository

class CourseRepositoryFileBased(private val context: Context) : CourseRepository {
    private val file: String = "courses.txt"
    private val separator: String = "|"

    init {
        val file = File(context.filesDir, file)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
              Log.e("CourseRepositoryFileBased: creating", e.message, e)
            }
        }
    }

    private fun toCourse(str: String): Course {
        val parts = str.split(separator)
        return Course(parseInt(parts[0]), parts[1], parseInt(parts[2]))
    }

    override fun findAll(): List<Course> {
        val courses = mutableListOf<Course>()
        val file = File(context.filesDir, file)

        try {
            FileInputStream(file).use {
                it.reader().use { reader ->
                    reader.forEachLine { str ->
                        courses.add(toCourse(str))
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("CourseRepositoryFileBased: reading", e.message, e)
        }

        Log.i("Repository findAll", "Courses: $courses")
        return courses
    }

    override fun get(id: Int): Course? {
        return findAll().firstOrNull { it.id == id }
    }

    override fun save(courses: MutableList<Course>): Boolean {
        try {
            context.openFileOutput(file, Context.MODE_PRIVATE).use {
                it.bufferedWriter().use { writer ->
                    courses.forEach { course ->
                        writer.write(toDb(course))
                        writer.newLine()
                    }
                }
            }
            return true
        } catch (e: IOException) {
            Log.e("CourseRepositoryFileBased: writing", e.message, e)
            return false
        }
    }

    private fun toDb(course: Course): String {
        return "${course.id}$separator${course.name}$separator${course.credits}"
    }

    override fun delete(id: Int) {
        val courses = findAll().toMutableList()
        val x = courses.removeIf { it.id == id }
        if (!x) {
            Log.e("CourseRepositoryFileBased: deleting", "Course not found")
        }
    }
}