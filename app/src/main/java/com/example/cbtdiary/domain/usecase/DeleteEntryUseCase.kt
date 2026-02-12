package com.example.cbtdiary.domain.usecase

import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.repository.DiaryRepository
import javax.inject.Inject

class DeleteEntryUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(entry: DiaryEntry) {
        repository.deleteEntry(entry)
    }
}
