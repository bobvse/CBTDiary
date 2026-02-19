package com.example.cbtdiary.domain.repository

import com.example.cbtdiary.domain.model.Conceptualization
import kotlinx.coroutines.flow.Flow

interface ConceptualizationRepository {
    fun getLatestVersion(): Flow<Conceptualization?>
    fun getAllVersions(): Flow<List<Conceptualization>>
    suspend fun getVersion(version: Int): Conceptualization?
    suspend fun save(conceptualization: Conceptualization): Long
    suspend fun deleteVersion(id: Long)
    suspend fun getVersionCount(): Int
    suspend fun getMaxVersion(): Int
}
