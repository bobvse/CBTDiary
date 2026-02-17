package com.example.cbtdiary.data.mapper

import com.example.cbtdiary.data.local.entity.DiaryEntryEntity
import com.example.cbtdiary.domain.model.DiaryEntry

fun DiaryEntryEntity.toDomain(): DiaryEntry {
    return DiaryEntry(
        id = id,
        situation = situation,
        thoughts = thoughts,
        emotions = emotions,
        bodyReaction = bodyReaction,
        actionReaction = actionReaction,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun DiaryEntry.toEntity(): DiaryEntryEntity {
    return DiaryEntryEntity(
        id = id,
        situation = situation,
        thoughts = thoughts,
        emotions = emotions,
        bodyReaction = bodyReaction,
        actionReaction = actionReaction,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
