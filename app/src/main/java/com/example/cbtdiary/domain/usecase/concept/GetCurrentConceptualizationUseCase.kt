package com.example.cbtdiary.domain.usecase.concept

import com.example.cbtdiary.domain.model.Conceptualization
import com.example.cbtdiary.domain.repository.ConceptualizationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentConceptualizationUseCase @Inject constructor(
    private val repository: ConceptualizationRepository
) {
    operator fun invoke(): Flow<Conceptualization?> {
        return repository.getLatestVersion()
    }
}
