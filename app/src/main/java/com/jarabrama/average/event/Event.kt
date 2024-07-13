package com.jarabrama.average.event

sealed class Event {
    data class CourseAddedEvent(val name: String, val credits: Int) : Event()
}