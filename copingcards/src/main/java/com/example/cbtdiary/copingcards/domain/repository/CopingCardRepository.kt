package com.example.cbtdiary.copingcards.domain.repository

import com.example.cbtdiary.copingcards.domain.model.CopingCard
import kotlinx.coroutines.flow.Flow

interface CopingCardRepository {
    fun getAllCards(): Flow<List<CopingCard>>
    fun getFavoriteCards(): Flow<List<CopingCard>>
    suspend fun getCardById(id: Long): CopingCard?
    suspend fun insertCard(card: CopingCard): Long
    suspend fun updateCard(card: CopingCard)
    suspend fun deleteCard(id: Long)
    suspend fun getCardCount(): Int
    suspend fun recordUsage(id: Long)
}
