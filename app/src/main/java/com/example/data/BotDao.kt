package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BotDao {
    @Query("SELECT * FROM bots ORDER BY addedTime DESC")
    fun getAllBots(): Flow<List<BotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBot(bot: BotEntity): Long

    @Update
    suspend fun updateBot(bot: BotEntity)

    @Delete
    suspend fun deleteBot(bot: BotEntity)

    @Query("UPDATE bots SET isRunning = :isRunning WHERE id = :id")
    suspend fun updateBotStatus(id: Int, isRunning: Boolean)
}
