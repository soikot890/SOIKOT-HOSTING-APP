package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ServerNode(
    val id: Int,
    val name: String,
    val ipAddress: String,
    val totalRamGb: Double = 1024.0, // 1 TB
    val cpuCores: Int = 128,
    val storageGb: Double = 8192.0, // 8 TB SSD
    val location: String = "Singapore West - Zone 1"
)

data class ServerLiveStats(
    val serverId: Int,
    val ramUsedGb: Double,
    val cpuUsedPercent: Double,
    val networkTrafficMbps: Double
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = BotRepository(database.botDao(), database.logDao())

    // UI Auth State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loggedInUserEmail = MutableStateFlow("azizulkan6@gmail.com")
    val loggedInUserEmail = _loggedInUserEmail.asStateFlow()

    // Bot List from Room
    val botList: StateFlow<List<BotEntity>> = repository.allBots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Log List from Room
    val logList: StateFlow<List<LogEntity>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 20 Powerhouse Servers (each 1 TB RAM)
    val servers = (1..20).map { i ->
        ServerNode(
            id = i,
            name = "ULTRABOT-NODE-${String.format("%02d", i)}",
            ipAddress = "103.178.45.${100 + i}",
            totalRamGb = 1024.0,
            cpuCores = 128,
            storageGb = 8192.0,
            location = when (i % 4) {
                0 -> "Singapore West - Zone 1"
                1 -> "Dhaka Gateway - Core 1"
                2 -> "Asia Pacific South - Zone 2"
                else -> "Tokyo East - Zone 3"
            }
        )
    }

    private val _serversLiveStats = MutableStateFlow<Map<Int, ServerLiveStats>>(
        (1..20).associateWith { id ->
            ServerLiveStats(
                serverId = id,
                ramUsedGb = 14.5 + Random.nextDouble(-0.5, 0.5),
                cpuUsedPercent = 0.5 + Random.nextDouble(0.1, 0.3),
                networkTrafficMbps = Random.nextDouble(2.0, 12.0)
            )
        }
    )
    val serversLiveStats: StateFlow<Map<Int, ServerLiveStats>> = _serversLiveStats.asStateFlow()

    private val _selectedServerId = MutableStateFlow(1)
    val selectedServerId: StateFlow<Int> = _selectedServerId.asStateFlow()

    fun selectServer(id: Int) {
        _selectedServerId.value = id
        viewModelScope.launch {
            val serverName = servers.find { it.id == id }?.name ?: "Unknown Node"
            repository.insertLog(LogEntity(message = "[SSH] Selected session changed to Server NODE #$id ($serverName). Synced connection secure.", type = "info"))
        }
    }

    // Live fluctuate stats
    private val _uptimeSeconds = MutableStateFlow(11200345L) // Init uptime
    val uptimeString: StateFlow<String> = _uptimeSeconds.map { totalSecs ->
        val days = totalSecs / (24 * 3600)
        val hours = (totalSecs % (24 * 3600)) / 3600
        val mins = (totalSecs % 3600) / 60
        val secs = totalSecs % 60
        "${days}d ${hours}h ${mins}m ${secs}s"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "129d 14h 23m 48s")

    private val _liveRamUsage = MutableStateFlow(40.5) // GB
    val liveRamUsage = _liveRamUsage.asStateFlow()

    private val _liveCpuUsage = MutableStateFlow(1.5) // %
    val liveCpuUsage = _liveCpuUsage.asStateFlow()

    private val _liveNetworkTraffic = MutableStateFlow("1.2 Gbps")
    val liveNetworkTraffic = _liveNetworkTraffic.asStateFlow()

    // Form inputs state
    var botName = MutableStateFlow("")
    var botType = MutableStateFlow("🤖 Discord Bot")
    var botToken = MutableStateFlow("")
    var botScript = MutableStateFlow("")

    val botTypes = listOf("🤖 Discord Bot", "📨 Telegram Bot", "💼 Slack Bot", "🕷️ Web Scraper", "⚡ API Gateway")

    init {
        // Increment Uptime and dynamic stats simulator for all 20 servers in parallel!
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _uptimeSeconds.value += 1
                
                val currentBots = botList.value
                val updatedStats = servers.associate { server ->
                    val runningCountOnServer = currentBots.count { it.isRunning && it.serverId == server.id }
                    
                    // Base RAM Usage: 14.5GB base + 28.6GB per active running bot (on 1024 GB RAM/1TB server structure)
                    val baseRam = 14.5 + (runningCountOnServer * 28.6)
                    val noiseRam = Random.nextDouble(-0.8, 0.8)
                    val ramUsed = (baseRam + noiseRam).coerceIn(8.0, 1024.0)
                    
                    // CPU Usage: 0.5% idle + 4.2% per active running container
                    val baseCpu = 0.5 + (runningCountOnServer * 4.2)
                    val noiseCpuLimit = if (runningCountOnServer > 0) 1.5 else 0.4
                    val noiseCpu = Random.nextDouble(-noiseCpuLimit, noiseCpuLimit)
                    val cpuUsed = (baseCpu + noiseCpu).coerceIn(0.1, 99.9)
                    
                    // Live Network Speed
                    val baseNet = (runningCountOnServer * 36.4) + Random.nextDouble(2.0, 18.0)
                    
                    server.id to ServerLiveStats(
                        serverId = server.id,
                        ramUsedGb = ramUsed,
                        cpuUsedPercent = cpuUsed,
                        networkTrafficMbps = baseNet
                    )
                }
                
                _serversLiveStats.value = updatedStats
                
                // Update selected server indicators for direct UI bindings
                val activeServerId = _selectedServerId.value
                val activeStats = updatedStats[activeServerId]
                if (activeStats != null) {
                    _liveRamUsage.value = activeStats.ramUsedGb
                    _liveCpuUsage.value = activeStats.cpuUsedPercent
                    _liveNetworkTraffic.value = String.format("%.1f Mbps", activeStats.networkTrafficMbps)
                }
            }
        }

        // Setup introductory logs if database is empty
        viewModelScope.launch {
            repository.allLogs.first().let { logs ->
                if (logs.isEmpty()) {
                    addInitLogs()
                }
            }
        }
    }

    private suspend fun addInitLogs() {
        val initLogs = listOf(
            LogEntity(message = "[SYSTEM] Initializing UltraBot Multi-Node Dedicated VPS Array (v2.5.0)...", type = "info"),
            LogEntity(message = "[SYSTEM] Core Backbone: Ultra-Premium Fiber Mesh (200 Gbps Total Uplink)", type = "info"),
            LogEntity(message = "[SYSTEM] Verified Physical Server Hardware array count: 20 dedicated nodes.", type = "info"),
            LogEntity(message = "[SYSTEM] Memory Allocated per Node: 1,024 GB (1 TB) DDR5 Register ECC.", type = "info"),
            LogEntity(message = "[SYSTEM] Total System Capacity limits: 20,480 GB RAM (20 TB total VPS memory).", type = "success"),
            LogEntity(message = "[SUCCESS] Dhaka Gateway Node #01, Tokyo East Node #03, and 18 additional gateways ONLINE", type = "success"),
            LogEntity(message = "[INFO] Client tunnel safely established. Ready to launch container microservices.", type = "info")
        )
        initLogs.forEach { repository.insertLog(it) }
    }

    fun login(email: String) {
        viewModelScope.launch {
            _loggedInUserEmail.value = email
            _isLoggedIn.value = true
            repository.insertLog(LogEntity(message = "[INFO] User $email signed in successfully.", type = "info"))
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoggedIn.value = false
            repository.insertLog(LogEntity(message = "[WARNING] User has signed out from control panel.", type = "warning"))
        }
    }

    fun deployBot() {
        val name = botName.value.trim()
        val type = botType.value
        val token = botToken.value.trim()
        val script = botScript.value.trim()

        if (name.isEmpty() || token.isEmpty()) {
            viewModelScope.launch {
                repository.insertLog(LogEntity(message = "[ERROR] Bot deployment failed: Missing name or access token.", type = "error"))
            }
            return
        }

        viewModelScope.launch {
            // Create in database as NOT running initially to simulate deployment sequence
            val bot = BotEntity(
                name = name,
                type = type,
                token = token,
                script = if (script.isEmpty()) "console.log('Bot running on 1TB Cluster');" else script,
                isRunning = false,
                serverId = _selectedServerId.value
            )
            
            val botId = repository.insertBot(bot).toInt()
            
            // Clear fields
            botName.value = ""
            botToken.value = ""
            botScript.value = ""

            // Play deployment simulation logs
            repository.insertLog(LogEntity(message = "[INFO] Preparing node container for '$name' [Type: $type]...", type = "info"))
            delay(800)
            repository.insertLog(LogEntity(message = "[INFO] Mounting isolated virtual memory cluster segment (+12.8 GB RAM limits)...", type = "info"))
            delay(600)
            repository.insertLog(LogEntity(message = "[INFO] Securely seeding host access credentials container...", type = "info"))
            delay(700)
            repository.insertLog(LogEntity(message = "[SUCCESS] Client bundle compiled successfully. PID allocated: ${Random.nextInt(1828, 9874)}.", type = "success"))
            delay(500)
            
            // Set running status to true
            repository.updateBotStatus(botId, true)
            repository.insertLog(LogEntity(message = "[SUCCESS] Bot bot-service '$name' is now ONLINE and operating on high-efficiency cluster!", type = "success"))
        }
    }

    fun toggleBotStatus(bot: BotEntity) {
        viewModelScope.launch {
            val newStatus = !bot.isRunning
            if (newStatus) {
                repository.insertLog(LogEntity(message = "[INFO] Rebooting bot container for '${bot.name}'...", type = "info"))
                delay(600)
                repository.updateBotStatus(bot.id, true)
                repository.insertLog(LogEntity(message = "[SUCCESS] Bot '${bot.name}' has been successfully started.", type = "success"))
            } else {
                repository.insertLog(LogEntity(message = "[WARNING] Terminating host process for bot '${bot.name}'...", type = "warning"))
                delay(500)
                repository.updateBotStatus(bot.id, false)
                repository.insertLog(LogEntity(message = "[SUCCESS] Container stopped. Recovered allocated memory capacity.", type = "success"))
            }
        }
    }

    fun deleteBot(bot: BotEntity) {
        viewModelScope.launch {
            repository.insertLog(LogEntity(message = "[WARNING] Purging system registry and disk storage for '${bot.name}'...", type = "warning"))
            delay(500)
            repository.deleteBot(bot)
            repository.insertLog(LogEntity(message = "[SUCCESS] Bot '${bot.name}' has been removed from host cluster.", type = "success"))
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
            repository.insertLog(LogEntity(message = "[INFO] Log console buffer cleared by owner.", type = "info"))
        }
    }
}
