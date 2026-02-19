package com.example.cbtdiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cbtdiary.domain.model.Alternative
import com.example.cbtdiary.domain.model.AutomaticThought
import com.example.cbtdiary.domain.model.BackgroundEvent
import com.example.cbtdiary.domain.model.CoreBelief
import com.example.cbtdiary.domain.model.EmotionEntry
import com.example.cbtdiary.domain.model.IntermediateBelief
import com.example.cbtdiary.domain.model.Trigger

@Entity(tableName = "conceptualizations")
data class ConceptualizationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val version: Int = 1,
    val versionNote: String = "",
    val background: List<BackgroundEvent> = emptyList(),
    val coreBeliefs: List<CoreBelief> = emptyList(),
    val intermediateBeliefs: List<IntermediateBelief> = emptyList(),
    val copingStrategies: List<String> = emptyList(),
    val triggers: List<Trigger> = emptyList(),
    val automaticThoughts: List<AutomaticThought> = emptyList(),
    val emotions: List<EmotionEntry> = emptyList(),
    val behavioralPatterns: List<String> = emptyList(),
    val alternatives: List<Alternative> = emptyList(),
    val strengths: List<String> = emptyList(),
    val goals: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
