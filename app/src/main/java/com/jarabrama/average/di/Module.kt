package com.jarabrama.average.di

import android.content.Context
import com.jarabrama.average.repository.CourseRepository
import com.jarabrama.average.repository.GradeRepository
import com.jarabrama.average.repository.impl.CourseRepositoryFileBased
import com.jarabrama.average.repository.impl.GradeRepositoryFileBased
import com.jarabrama.average.service.impl.CourseServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.jarabrama.average.service.CourseService
import com.jarabrama.average.service.GradeService
import com.jarabrama.average.service.impl.GradeServiceImpl

@Module
@InstallIn(SingletonComponent::class)
class Module {
    @Provides
    fun provideCourseRepository(@ApplicationContext context: Context): CourseRepository {
        return CourseRepositoryFileBased(context)
    }

    @Provides
    fun provideCourseService(courseRepository: CourseRepository): CourseService {
        return CourseServiceImpl(courseRepository)
    }

    @Provides
    fun provideGradeRepository(@ApplicationContext context: Context): GradeRepository {
        return GradeRepositoryFileBased(context)
    }

    @Provides
    fun provideGradeService(gradeRepository: GradeRepository): GradeService {
        return GradeServiceImpl(gradeRepository)
    }
}