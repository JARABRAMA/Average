package com.jarabrama.average.di

import com.jarabrama.average.ui.viewmodel.ExpandedCourseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import java.util.IdentityHashMap

@AssistedFactory
interface ExpandedCourseAssistedFactory {
    fun create(courseId: Int): ExpandedCourseViewModel
}