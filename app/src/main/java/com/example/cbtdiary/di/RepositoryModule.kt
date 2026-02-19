package com.example.cbtdiary.di

import com.example.cbtdiary.data.repository.ConceptualizationRepositoryImpl
import com.example.cbtdiary.data.repository.DiaryRepositoryImpl
import com.example.cbtdiary.domain.repository.ConceptualizationRepository
import com.example.cbtdiary.domain.repository.DiaryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(
        diaryRepositoryImpl: DiaryRepositoryImpl
    ): DiaryRepository

    @Binds
    @Singleton
    abstract fun bindConceptualizationRepository(
        conceptualizationRepositoryImpl: ConceptualizationRepositoryImpl
    ): ConceptualizationRepository
}
