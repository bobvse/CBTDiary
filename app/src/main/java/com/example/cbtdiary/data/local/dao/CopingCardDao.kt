package com.example.cbtdiary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cbtdiary.data.local.entity.CopingCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CopingCardDao {

    @Query("SELECT * FROM coping_cards ORDER BY updatedAt DESC")
    fun getAllCards(): Flow<List<CopingCardEntity>>

    @Query("SELECT * FROM coping_cards WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteCards(): Flow<List<CopingCardEntity>>

    @Query("SELECT * FROM coping_cards WHERE id = :id")
    suspend fun getCardById(id: Long): CopingCardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CopingCardEntity): Long

    @Update
    suspend fun updateCard(card: CopingCardEntity)

    @Query("DELETE FROM coping_cards WHERE id = :id")
    suspend fun deleteCard(id: Long)

    @Query("SELECT COUNT(*) FROM coping_cards")
    suspend fun getCardCount(): Int

    @Query("UPDATE coping_cards SET usageCount = usageCount + 1, lastUsedAt = :timestamp WHERE id = :id")
    suspend fun recordUsage(id: Long, timestamp: Long)
}
