package com.example.cbtdiary.domain.repository

import com.example.cbtdiary.domain.model.DiaryEntry
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    fun getAllEntries(): Flow<List<DiaryEntry>>
    suspend fun getEntryById(id: Long): DiaryEntry?
    suspend fun insertEntry(entry: DiaryEntry): Long
    suspend fun updateEntry(entry: DiaryEntry)
    suspend fun deleteEntry(entry: DiaryEntry)
}
