package com.example.cbtdiary.data.repository

import com.example.cbtdiary.data.local.dao.DiaryEntryDao
import com.example.cbtdiary.data.mapper.toDomain
import com.example.cbtdiary.data.mapper.toEntity
import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val dao: DiaryEntryDao
) : DiaryRepository {
    
    override fun getAllEntries(): Flow<List<DiaryEntry>> {
        return dao.getAllEntries().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getEntryById(id: Long): DiaryEntry? {
        return dao.getEntryById(id)?.toDomain()
    }
    
    override suspend fun insertEntry(entry: DiaryEntry): Long {
        return dao.insertEntry(entry.toEntity())
    }
    
    override suspend fun updateEntry(entry: DiaryEntry) {
        dao.updateEntry(entry.toEntity())
    }
    
    override suspend fun deleteEntry(entry: DiaryEntry) {
        dao.deleteEntry(entry.toEntity())
    }
}
