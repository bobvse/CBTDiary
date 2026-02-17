package com.example.cbtdiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val situation: String = "",
    val thoughts: String = "",
    val emotions: List<String> = emptyList(),
    val bodyReaction: String = "",
    val actionReaction: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
