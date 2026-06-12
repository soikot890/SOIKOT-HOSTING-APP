package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bots")
data class BotEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val token: String,
    val script: String,
    val isRunning: Boolean,
    val serverId: Int = 1,
    val addedTime: Long = System.currentTimeMillis()
)
