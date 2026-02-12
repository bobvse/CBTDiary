package com.example.cbtdiary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cbtdiary.data.local.dao.DiaryEntryDao
import com.example.cbtdiary.data.local.entity.DiaryEntryEntity

@Database(
    entities = [DiaryEntryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryEntryDao(): DiaryEntryDao
}
