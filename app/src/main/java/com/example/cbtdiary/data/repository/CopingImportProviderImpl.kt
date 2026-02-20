package com.example.cbtdiary.data.repository

import com.example.cbtdiary.copingcards.domain.model.CardSourceType
import com.example.cbtdiary.copingcards.domain.model.ImportSuggestion
import com.example.cbtdiary.copingcards.domain.repository.CopingImportProvider
import com.example.cbtdiary.domain.repository.ConceptualizationRepository
import com.example.cbtdiary.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CopingImportProviderImpl @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val conceptRepository: ConceptualizationRepository
) : CopingImportProvider {

    override suspend fun getSmerSuggestions(): List<ImportSuggestion> {
        val entries = diaryRepository.getAllEntries().first()
        val suggestions = mutableListOf<ImportSuggestion>()

        entries.forEach { entry ->
            if (entry.thoughts.isNotBlank()) {
                suggestions.add(
                    ImportSuggestion(
                        text = entry.thoughts,
                        secondaryText = entry.situation,
                        sourceType = CardSourceType.SMER_IMPORT,
                        sourceId = entry.id
                    )
                )
            }
        }

        return suggestions.distinctBy { it.text }
    }

    override suspend fun getConceptSuggestions(): List<ImportSuggestion> {
        val concept = conceptRepository.getLatestVersion().first() ?: return emptyList()
        val suggestions = mutableListOf<ImportSuggestion>()

        concept.automaticThoughts.forEach { thought ->
            suggestions.add(
                ImportSuggestion(
                    text = thought.text,
                    secondaryText = thought.distortionType?.name ?: "",
                    sourceType = CardSourceType.CONCEPT_IMPORT
                )
            )
        }

        concept.alternatives.forEach { alt ->
            if (alt.oldThought.isNotBlank()) {
                suggestions.add(
                    ImportSuggestion(
                        text = alt.oldThought,
                        secondaryText = alt.newThought,
                        sourceType = CardSourceType.CONCEPT_IMPORT
                    )
                )
            }
        }

        return suggestions
    }
}
