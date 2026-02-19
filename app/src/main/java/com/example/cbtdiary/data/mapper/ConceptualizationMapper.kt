package com.example.cbtdiary.data.mapper

import com.example.cbtdiary.data.local.entity.ConceptualizationEntity
import com.example.cbtdiary.domain.model.Conceptualization

fun ConceptualizationEntity.toDomain(): Conceptualization {
    return Conceptualization(
        id = id,
        version = version,
        versionNote = versionNote,
        background = background,
        coreBeliefs = coreBeliefs,
        intermediateBeliefs = intermediateBeliefs,
        copingStrategies = copingStrategies,
        triggers = triggers,
        automaticThoughts = automaticThoughts,
        emotions = emotions,
        behavioralPatterns = behavioralPatterns,
        alternatives = alternatives,
        strengths = strengths,
        goals = goals,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Conceptualization.toEntity(): ConceptualizationEntity {
    return ConceptualizationEntity(
        id = id,
        version = version,
        versionNote = versionNote,
        background = background,
        coreBeliefs = coreBeliefs,
        intermediateBeliefs = intermediateBeliefs,
        copingStrategies = copingStrategies,
        triggers = triggers,
        automaticThoughts = automaticThoughts,
        emotions = emotions,
        behavioralPatterns = behavioralPatterns,
        alternatives = alternatives,
        strengths = strengths,
        goals = goals,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
