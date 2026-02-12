package com.example.cbtdiary.domain.usecase

import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.repository.DiaryRepository
import javax.inject.Inject

class SaveEntryUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(entry: DiaryEntry): Long {
        // TODO: Заменить на инжектируемый Clock для тестируемости
        val currentTime = System.currentTimeMillis()
        val entryToSave = if (entry.id == DiaryEntry.NEW_ENTRY_ID) {
            entry.copy(
                createdAt = currentTime,
                updatedAt = currentTime
            )
        } else {
            entry.copy(updatedAt = currentTime)
        }
        
        return if (entryToSave.id == DiaryEntry.NEW_ENTRY_ID) {
            repository.insertEntry(entryToSave)
        } else {
            repository.updateEntry(entryToSave)
            entryToSave.id
        }
    }
}
