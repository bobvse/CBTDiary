package com.example.cbtdiary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cbtdiary.data.local.entity.ConceptualizationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConceptualizationDao {

    @Query("SELECT * FROM conceptualizations ORDER BY version DESC LIMIT 1")
    fun getLatestVersion(): Flow<ConceptualizationEntity?>

    @Query("SELECT * FROM conceptualizations ORDER BY version DESC")
    fun getAllVersions(): Flow<List<ConceptualizationEntity>>

    @Query("SELECT * FROM conceptualizations WHERE version = :version")
    suspend fun getVersion(version: Int): ConceptualizationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ConceptualizationEntity): Long

    @Query("DELETE FROM conceptualizations WHERE id = :id")
    suspend fun deleteVersion(id: Long)

    @Query("SELECT COUNT(*) FROM conceptualizations")
    suspend fun getVersionCount(): Int

    @Query("SELECT MAX(version) FROM conceptualizations")
    suspend fun getMaxVersion(): Int?
}
