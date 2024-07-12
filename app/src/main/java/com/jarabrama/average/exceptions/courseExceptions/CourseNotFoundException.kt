package com.jarabrama.average.exceptions.courseExceptions

class CourseNotFoundException(id: Int): CourseException("course id '${id}' not found") {
}