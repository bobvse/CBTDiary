package com.example.cbtdiary.data.repository

import com.example.cbtdiary.copingcards.domain.model.CopingCard
import com.example.cbtdiary.copingcards.domain.repository.CopingCardRepository
import com.example.cbtdiary.data.local.dao.CopingCardDao
import com.example.cbtdiary.data.mapper.toDomain
import com.example.cbtdiary.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CopingCardRepositoryImpl @Inject constructor(
    private val dao: CopingCardDao
) : CopingCardRepository {

    override fun getAllCards(): Flow<List<CopingCard>> {
        return dao.getAllCards().map { list -> list.map { it.toDomain() } }
    }

    override fun getFavoriteCards(): Flow<List<CopingCard>> {
        return dao.getFavoriteCards().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getCardById(id: Long): CopingCard? {
        return dao.getCardById(id)?.toDomain()
    }

    override suspend fun insertCard(card: CopingCard): Long {
        val now = System.currentTimeMillis()
        return dao.insertCard(card.copy(createdAt = now, updatedAt = now).toEntity())
    }

    override suspend fun updateCard(card: CopingCard) {
        dao.updateCard(card.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteCard(id: Long) {
        dao.deleteCard(id)
    }

    override suspend fun getCardCount(): Int {
        return dao.getCardCount()
    }

    override suspend fun recordUsage(id: Long) {
        dao.recordUsage(id, System.currentTimeMillis())
    }
}
