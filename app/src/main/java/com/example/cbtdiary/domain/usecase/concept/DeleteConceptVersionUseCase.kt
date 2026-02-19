package com.example.cbtdiary.domain.usecase.concept

import com.example.cbtdiary.domain.repository.ConceptualizationRepository
import javax.inject.Inject

class DeleteConceptVersionUseCase @Inject constructor(
    private val repository: ConceptualizationRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteVersion(id)
    }
}
