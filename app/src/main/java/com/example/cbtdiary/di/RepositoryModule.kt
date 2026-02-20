package com.example.cbtdiary.di

import com.example.cbtdiary.copingcards.domain.repository.CopingCardRepository
import com.example.cbtdiary.copingcards.domain.repository.CopingImportProvider
import com.example.cbtdiary.data.repository.ConceptualizationRepositoryImpl
import com.example.cbtdiary.data.repository.CopingCardRepositoryImpl
import com.example.cbtdiary.data.repository.CopingImportProviderImpl
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

    @Binds
    @Singleton
    abstract fun bindCopingCardRepository(
        copingCardRepositoryImpl: CopingCardRepositoryImpl
    ): CopingCardRepository

    @Binds
    @Singleton
    abstract fun bindCopingImportProvider(
        copingImportProviderImpl: CopingImportProviderImpl
    ): CopingImportProvider
}
