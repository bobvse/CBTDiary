package com.example.cbtdiary.di

import android.content.Context
import androidx.room.Room
import com.example.cbtdiary.data.local.DiaryDatabase
import com.example.cbtdiary.data.local.dao.ConceptualizationDao
import com.example.cbtdiary.data.local.dao.DiaryEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DiaryDatabase {
        return Room.databaseBuilder(
            context,
            DiaryDatabase::class.java,
            "diary_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDiaryEntryDao(database: DiaryDatabase): DiaryEntryDao {
        return database.diaryEntryDao()
    }

    @Provides
    fun provideConceptualizationDao(database: DiaryDatabase): ConceptualizationDao {
        return database.conceptualizationDao()
    }
}
