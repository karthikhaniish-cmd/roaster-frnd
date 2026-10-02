package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoastDao {
    @Query("SELECT * FROM roasts ORDER BY timestamp DESC")
    fun getAllRoasts(): Flow<List<RoastEntity>>

    @Query("SELECT * FROM roasts WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteRoasts(): Flow<List<RoastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoast(roast: RoastEntity): Long

    @Update
    suspend fun updateRoast(roast: RoastEntity)

    @Delete
    suspend fun deleteRoast(roast: RoastEntity)

    @Query("DELETE FROM roasts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE roasts SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("DELETE FROM roasts")
    suspend fun clearAll()
}
