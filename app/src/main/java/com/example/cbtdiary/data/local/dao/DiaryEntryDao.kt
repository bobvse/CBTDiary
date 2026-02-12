package com.example.cbtdiary.data.local.dao

import androidx.room.*
import com.example.cbtdiary.data.local.entity.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM diary_entries ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<DiaryEntryEntity>>
    
    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): DiaryEntryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntryEntity): Long
    
    @Update
    suspend fun updateEntry(entry: DiaryEntryEntity)
    
    @Delete
    suspend fun deleteEntry(entry: DiaryEntryEntity)
}
