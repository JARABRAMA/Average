package com.jarabrama.average.service.impl

import com.jarabrama.average.exceptions.courseExceptions.CourseNotFoundException
import com.jarabrama.average.exceptions.gradeExceptions.GradeNotFoundException
import com.jarabrama.average.exceptions.gradeExceptions.InvalidPercentageException
import com.jarabrama.average.exceptions.gradeExceptions.SavingGradeException
import com.jarabrama.average.model.Grade
import com.jarabrama.average.repository.GradeRepository
import com.jarabrama.average.service.GradeService

class GradeServiceImpl(private val gradeRepository: GradeRepository) : GradeService {

    override fun findAll(): List<Grade> = gradeRepository.findAll()
    override fun findAllByCourseId(courseId: Int): List<Grade> =
        gradeRepository.findAllByCourseId(courseId)

    override fun newGrade(
        courseId: Int,
        name: String,
        qualification: Double,
        percentage: Double
    ): Grade {
        // percentage validation
        val restingPercentage = 100.0 - (findAllByCourseId(courseId).sumOf { it.percentage })
        if (percentage > restingPercentage) {
            throw InvalidPercentageException(
                restingPercentage = restingPercentage,
                inputPercentage = percentage
            )
        }

        val grades: MutableList<Grade> = gradeRepository.findAll()
        val newGrade = Grade(
            id = grades.size,
            courseId = courseId,
            name = name,
            qualification = qualification,
            percentage = percentage
        )
        grades.add(newGrade)
        val saved: Boolean = gradeRepository.save(grades)
        if (saved) {
            return newGrade
        } else {
            throw SavingGradeException(grade = newGrade)
        }
    }

    override fun update(grade: Grade): Grade {
        val grades = gradeRepository.findAll()
        val index: Int = grades.indexOf(grade)
        if (-1 != index) {
            grades[index] = grade
            return grade
        } else {
            throw GradeNotFoundException(grade.id)
        }
    }

    override fun delete(id: Int) {
        val foundedGrade = gradeRepository.get(id) ?: throw GradeNotFoundException(id)
        gradeRepository.delete(foundedGrade)
    }

    override fun get(id: Int): Grade = gradeRepository.get(id) ?: throw CourseNotFoundException(id)

    override fun getAverage(courseId: Int): Double {
        return findAllByCourseId(courseId).sumOf { it.qualification * (it.percentage / 100) }
    }
}