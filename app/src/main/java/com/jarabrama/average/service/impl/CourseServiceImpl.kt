package com.jarabrama.average.service.impl

import android.util.Log
import androidx.compose.runtime.currentRecomposeScope
import com.jarabrama.average.exceptions.courseExceptions.CourseException
import com.jarabrama.average.exceptions.courseExceptions.CourseNotFoundException
import com.jarabrama.average.model.Course
import com.jarabrama.average.repository.CourseRepository
import com.jarabrama.average.service.CourseService
import com.jarabrama.average.service.GradeService
import com.jarabrama.average.utils.Functions


class CourseServiceImpl(
    private val courseRepository: CourseRepository,
    private val gradeService: GradeService
) : CourseService {

    override fun findAll(): List<Course> {
        val courses = courseRepository.findAll();
        return courses
    }

    override fun newCourse(name: String, credits: Int): Course {
        val courses = courseRepository.findAll().toMutableList()
        val course = Course(courses.size, name, credits)
        courses.add(course)
        val save = courseRepository.save(courses)
        if (!save) {
            throw CourseException("Can not save the curse")
        }
        Log.i("Course Service: Adding Course", "course added $course")
        return course
    }

    override fun update(course: Course): Course {
        val courses = findAll().toMutableList()
        val index: Int = courses.indexOfFirst { it.id == course.id }
        if (index == -1) {
            throw CourseNotFoundException(course.id)
        }
        courses[index] = course;
        courseRepository.save(courses)
        return course
    }

    override fun delete(id: Int) {
        courseRepository.get(id)
            ?: throw CourseNotFoundException(id) // check if the note exists
        gradeService.findAllByCourseId(id).forEach {
            gradeService.delete(it.id) // delete all the grades of the course
        }
        courseRepository.delete(id)
    }

    override fun get(id: Int): Course {
        return courseRepository.get(id) ?: throw CourseNotFoundException(id)
    }

    private fun getAverage(id: Int): Double {
        return gradeService.findAllByCourseId(id).sumOf { it.qualification * (it.percentage / 100) }
    }


    override fun getAverages(): Map<Int, String> {

        val averages: MutableMap<Int, String> = mutableMapOf()
        findAll().forEach {
            val average = Functions.formatDecimal(getAverage(it.id))
            averages[it.id] = average
        }
        return averages
    }

    override fun getAnalysis(maxQualification: Double, minQualification: Double): String {
        val courses = findAll()
        val expectedAverages = courses.map { gradeService.getExpectedAverage(it.id) }

        val expectedCreditAverage =
            expectedAverages.sum() / expectedAverages.size

        val formatDecimalCreditAverage = Functions.formatDecimal(expectedCreditAverage)
        return if (expectedCreditAverage > maxQualification) {
            "You will not reach your goal the average of needed qualification overcomes the maximum " +
                    "qualification limit: $formatDecimalCreditAverage"
        } else if (expectedCreditAverage < minQualification) {
            "At this moment you are above your goal average, keep it up!"
        } else {
            "You need to keep a qualification average of $formatDecimalCreditAverage to reach " +
                    "your average goal"
        }
    }

    override fun getCreditAverage(): Double {
        val courses = findAll()
        val currentAverages = courses.associateBy({ it.id }, { getAverage(it.id) })
        val totalCredits = courses.sumOf { it.credits }
        return (courses.sumOf { it.credits * (currentAverages[it.id] ?: 0.0) }) / totalCredits
    }
}