package com.example.cbtdiary.domain.usecase.concept

import com.example.cbtdiary.domain.model.Conceptualization
import com.example.cbtdiary.domain.repository.ConceptualizationRepository
import javax.inject.Inject

class SaveConceptualizationUseCase @Inject constructor(
    private val repository: ConceptualizationRepository
) {
    suspend operator fun invoke(conceptualization: Conceptualization, versionNote: String = ""): Long {
        val maxVersion = repository.getMaxVersion()
        val now = System.currentTimeMillis()
        val newConcept = conceptualization.copy(
            id = 0,
            version = maxVersion + 1,
            versionNote = versionNote,
            createdAt = now,
            updatedAt = now
        )
        return repository.save(newConcept)
    }
}
