package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM console_logs ORDER BY timestamp ASC")
    fun getAllLogs(): Flow<List<LogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEntity): Long

    @Query("DELETE FROM console_logs")
    suspend fun clearLogs()

    @Query("DELETE FROM console_logs WHERE id NOT IN (SELECT id FROM console_logs ORDER BY timestamp DESC LIMIT 200)")
    suspend fun trimLogs()
}
