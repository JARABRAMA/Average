package com.jarabrama.average.di

import com.jarabrama.average.ui.viewmodel.NewGradeViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface NewGradeAssistedFactory {
    fun create(courseId: Int): NewGradeViewModel
}