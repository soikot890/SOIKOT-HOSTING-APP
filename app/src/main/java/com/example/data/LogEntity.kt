package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "console_logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val botId: Int? = null,
    val message: String,
    val type: String, // "info", "success", "warning", "error"
    val timestamp: Long = System.currentTimeMillis()
)
