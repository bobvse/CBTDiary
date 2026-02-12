package com.example.cbtdiary.domain.usecase

import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllEntriesUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    operator fun invoke(): Flow<List<DiaryEntry>> {
        return repository.getAllEntries()
    }
}
