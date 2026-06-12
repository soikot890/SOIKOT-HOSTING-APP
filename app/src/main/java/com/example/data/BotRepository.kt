package com.example.data

import kotlinx.coroutines.flow.Flow

class BotRepository(private val botDao: BotDao, private val logDao: LogDao) {
    val allBots: Flow<List<BotEntity>> = botDao.getAllBots()
    val allLogs: Flow<List<LogEntity>> = logDao.getAllLogs()

    suspend fun insertBot(bot: BotEntity): Long = botDao.insertBot(bot)

    suspend fun updateBot(bot: BotEntity) = botDao.updateBot(bot)

    suspend fun deleteBot(bot: BotEntity) = botDao.deleteBot(bot)

    suspend fun updateBotStatus(id: Int, isRunning: Boolean) = botDao.updateBotStatus(id, isRunning)

    suspend fun insertLog(log: LogEntity): Long {
        val id = logDao.insertLog(log)
        logDao.trimLogs()
        return id
    }

    suspend fun clearLogs() = logDao.clearLogs()
}
