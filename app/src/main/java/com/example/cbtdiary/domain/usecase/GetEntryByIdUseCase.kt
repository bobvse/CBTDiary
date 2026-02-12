package com.example.cbtdiary.domain.usecase

import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.repository.DiaryRepository
import javax.inject.Inject

class GetEntryByIdUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(id: Long): DiaryEntry? {
        return repository.getEntryById(id)
    }
}
