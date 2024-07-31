package com.jarabrama.average.di

import android.content.Context
import com.jarabrama.average.repository.CourseRepository
import com.jarabrama.average.repository.GradeRepository
import com.jarabrama.average.repository.SettingsRepository
import com.jarabrama.average.repository.impl.CourseRepositoryFileBased
import com.jarabrama.average.repository.impl.GradeRepositoryFileBased
import com.jarabrama.average.repository.impl.SettingsRepositoryFileBased
import com.jarabrama.average.service.impl.CourseServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.jarabrama.average.service.CourseService
import com.jarabrama.average.service.GradeService
import com.jarabrama.average.service.SettingsService
import com.jarabrama.average.service.impl.GradeServiceImpl
import com.jarabrama.average.service.impl.SettingsServiceImpl

@Module
@InstallIn(SingletonComponent::class)
class Module {
    @Provides
    fun provideCourseRepository(@ApplicationContext context: Context): CourseRepository {
        return CourseRepositoryFileBased(context)
    }

    @Provides
    fun provideCourseService(courseRepository: CourseRepository, gradeService: GradeService): CourseService {
        return CourseServiceImpl(courseRepository, gradeService)
    }

    @Provides
    fun provideGradeRepository(@ApplicationContext context: Context): GradeRepository {
        return GradeRepositoryFileBased(context)
    }

    @Provides
    fun provideGradeService(
        gradeRepository: GradeRepository,
        settingsRepository: SettingsRepository
    ): GradeService {
        return GradeServiceImpl(gradeRepository, settingsRepository)
    }

    @Provides
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepositoryFileBased(context)
    }

    @Provides
    fun provideSettingsService(settingsRepository: SettingsRepository): SettingsService {
        return SettingsServiceImpl(settingsRepository)
    }
}