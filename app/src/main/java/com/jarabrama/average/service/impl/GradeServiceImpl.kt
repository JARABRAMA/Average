package com.jarabrama.average.service.impl

import com.jarabrama.average.exceptions.courseExceptions.CourseNotFoundException
import com.jarabrama.average.exceptions.gradeExceptions.GradeNotFoundException
import com.jarabrama.average.exceptions.gradeExceptions.InvalidPercentageException
import com.jarabrama.average.exceptions.gradeExceptions.InvalidQualificationException
import com.jarabrama.average.exceptions.gradeExceptions.SavingGradeException
import com.jarabrama.average.model.Grade
import com.jarabrama.average.repository.GradeRepository
import com.jarabrama.average.repository.SettingsRepository
import com.jarabrama.average.service.GradeService
import com.jarabrama.average.utils.Functions

class GradeServiceImpl(
    private val gradeRepository: GradeRepository,
    private val settingsRepository: SettingsRepository
) : GradeService {

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
        val availablePercentage = getAvailablePercentage(courseId)
        val settings = settingsRepository.getSettings()
        if (percentage > availablePercentage) {
            throw InvalidPercentageException(
                restingPercentage = availablePercentage,
                inputPercentage = percentage
            )
        }
        if (qualification !in settings.minQualification..settings.maxQualification) {
            throw InvalidQualificationException(qualification)
        }

        val grades: MutableList<Grade> = gradeRepository.findAll()
        val newId: Int = (gradeRepository.findAll().maxOfOrNull { it.id } ?: 0) + 1
        val newGrade = Grade(
            id = newId,
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

    override fun getAvailablePercentage(courseId: Int) =
        100.0 - (findAllByCourseId(courseId).sumOf { it.percentage })

    override fun update(grade: Grade): Grade {
        val grades = gradeRepository.findAll()
        val index = grades.indexOfFirst { it.id == grade.id }
        if (-1 != index) {
            grades[index] = grade
            gradeRepository.save(grades)
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

    override fun getAverage(courseId: Int): Double =
        findAllByCourseId(courseId).sumOf { it.qualification * (it.percentage / 100) }

    override fun getExpectedAverage(courseId: Int): Double {
        val currentAverage = getAverage(courseId)
        val availablePercentage = getAvailablePercentage(courseId)
        val settings = settingsRepository.getSettings()
        val goal = settings.goal
        return (goal - currentAverage) / (availablePercentage / 100)
    }


    override fun getAnalysis(courseId: Int): String {
        val currentAverage = getAverage(courseId)
        val availablePercentage = getAvailablePercentage(courseId)
        val settings = settingsRepository.getSettings()
        val goal = settings.goal
        val maxQualification = settings.maxQualification
        val neededQualification =
            (goal - currentAverage) / (availablePercentage / 100)

        if (availablePercentage == 0.0) {
            return if (currentAverage > goal) {
                "Congratulations! you have overcome your goal with an average of $currentAverage"
            } else if (currentAverage == goal) {
                "Congratulations! you have reach your goal of $goal"
            } else {
                "You do not have reach your goal, your average was $currentAverage"
            }
        } else {
            return if (neededQualification > maxQualification) {
                "You will not reach your goal on this occasion, the needed " +
                        "qualification overcomes the qualification limit: $neededQualification"
            } else if (neededQualification <= settings.minQualification) {
                "Great! you have reach your goal before complete total qualification percentage"
            } else {
                "You need a qualification of ${Functions.formatDecimal(neededQualification)} in the $availablePercentage% of resting evaluative percentage"
            }
        }
    }

    override fun getSimpleAverage(): Double {
        val grades = findAll()
        return grades.sumOf { it.qualification } / grades.size
    }
}