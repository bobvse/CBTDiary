package com.example.cbtdiary.data.repository

import com.example.cbtdiary.data.local.dao.ConceptualizationDao
import com.example.cbtdiary.data.mapper.toDomain
import com.example.cbtdiary.data.mapper.toEntity
import com.example.cbtdiary.domain.model.Conceptualization
import com.example.cbtdiary.domain.repository.ConceptualizationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConceptualizationRepositoryImpl @Inject constructor(
    private val dao: ConceptualizationDao
) : ConceptualizationRepository {

    override fun getLatestVersion(): Flow<Conceptualization?> {
        return dao.getLatestVersion().map { it?.toDomain() }
    }

    override fun getAllVersions(): Flow<List<Conceptualization>> {
        return dao.getAllVersions().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getVersion(version: Int): Conceptualization? {
        return dao.getVersion(version)?.toDomain()
    }

    override suspend fun save(conceptualization: Conceptualization): Long {
        return dao.insert(conceptualization.toEntity())
    }

    override suspend fun deleteVersion(id: Long) {
        dao.deleteVersion(id)
    }

    override suspend fun getVersionCount(): Int {
        return dao.getVersionCount()
    }

    override suspend fun getMaxVersion(): Int {
        return dao.getMaxVersion() ?: 0
    }
}
