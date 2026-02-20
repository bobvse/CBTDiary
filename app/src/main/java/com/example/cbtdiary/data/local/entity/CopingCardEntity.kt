package com.example.cbtdiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coping_cards")
data class CopingCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val frontText: String = "",
    val backText: String = "",
    val strategies: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val colorIndex: Int = 0,
    val usageCount: Int = 0,
    val lastUsedAt: Long? = null,
    val sourceType: String = "MANUAL",
    val sourceId: Long? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
