package com.example.cbtdiary.data.mapper

import com.example.cbtdiary.copingcards.domain.model.CardSourceType
import com.example.cbtdiary.copingcards.domain.model.CopingCard
import com.example.cbtdiary.data.local.entity.CopingCardEntity

fun CopingCardEntity.toDomain(): CopingCard {
    return CopingCard(
        id = id,
        frontText = frontText,
        backText = backText,
        strategies = strategies,
        tags = tags,
        isFavorite = isFavorite,
        colorIndex = colorIndex,
        usageCount = usageCount,
        lastUsedAt = lastUsedAt,
        sourceType = try { CardSourceType.valueOf(sourceType) } catch (_: Exception) { CardSourceType.MANUAL },
        sourceId = sourceId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun CopingCard.toEntity(): CopingCardEntity {
    return CopingCardEntity(
        id = id,
        frontText = frontText,
        backText = backText,
        strategies = strategies,
        tags = tags,
        isFavorite = isFavorite,
        colorIndex = colorIndex,
        usageCount = usageCount,
        lastUsedAt = lastUsedAt,
        sourceType = sourceType.name,
        sourceId = sourceId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
