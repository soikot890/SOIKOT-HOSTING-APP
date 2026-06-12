package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BotEntity
import com.example.data.LogEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Observe StateFlow states
    val userEmail by viewModel.loggedInUserEmail.collectAsState()
    val bots by viewModel.botList.collectAsState()
    val logs by viewModel.logList.collectAsState()
    val uptime by viewModel.uptimeString.collectAsState()
    val ramUsed by viewModel.liveRamUsage.collectAsState()
    val cpuUsed by viewModel.liveCpuUsage.collectAsState()
    val networkbySec by viewModel.liveNetworkTraffic.collectAsState()
    val selectedServerId by viewModel.selectedServerId.collectAsState()
    val serversLiveStats by viewModel.serversLiveStats.collectAsState()
    val activeServerNode = remember(selectedServerId) {
        viewModel.servers.find { it.id == selectedServerId } ?: viewModel.servers.first()
    }

    // Form states
    val botName by viewModel.botName.collectAsState()
    val botType by viewModel.botType.collectAsState()
    val botToken by viewModel.botToken.collectAsState()
    val botScript by viewModel.botScript.collectAsState()

    var showSpecsModal by remember { mutableStateOf(false) }
    var showActiveTokenId by remember { mutableStateOf<Int?>(null) }
    var selectedBotForScriptMsg by remember { mutableStateOf<BotEntity?>(null) }

    // Dynamic background like CSS
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07070F))
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF8A2BE2).copy(alpha = 0.15f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(0f, size.height * 0.2f),
                        radius = size.width * 0.7f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00D4FF).copy(alpha = 0.1f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width, size.height * 0.8f),
                        radius = size.width * 0.7f
                    )
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Elegant Control Panel Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left avatar cluster
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF8A2BE2), Color(0xFF00D4FF))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AK",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        // Active online status green pulse dot
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .align(Alignment.BottomEnd)
                                .background(Color(0xFF0A0A12), CircleShape)
                                .padding(1.5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF00E676), CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Azizul Kan",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = userEmail,
                            color = Color(0xFF888899),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Tier badge and logout button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF8A2BE2), Color(0xFF00D4FF))
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "ULTRA TIER",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                            .testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Log Out",
                            tint = Color(0xFFFF1744),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 20 Servers Cluster Controller Grid (Total 20 TB VPS RAM)
                ServerClusterGrid(
                    servers = viewModel.servers,
                    selectedServerId = selectedServerId,
                    bots = bots,
                    serversLiveStats = serversLiveStats,
                    onServerSelect = { viewModel.selectServer(it) }
                )

                // Server cluster status indicator card (1TB RAM)
                InteractiveServerCard(
                    selectedServer = activeServerNode,
                    uptime = uptime,
                    ramUsed = ramUsed,
                    cpuUsed = cpuUsed,
                    networkbySec = networkbySec,
                    onSpecsClick = { showSpecsModal = true }
                )

                // Deployment Controls Card
                BotDeploymentForm(
                    botName = botName,
                    botType = botType,
                    botToken = botToken,
                    botScript = botScript,
                    botTypes = viewModel.botTypes,
                    selectedServerId = selectedServerId,
                    onNameChange = { viewModel.botName.value = it },
                    onTypeChange = { viewModel.botType.value = it },
                    onTokenChange = { viewModel.botToken.value = it },
                    onScriptChange = { viewModel.botScript.value = it },
                    onDeploy = { viewModel.deployBot() }
                )

                // Deployed Container List
                ActiveContainersCard(
                    bots = bots,
                    showActiveTokenId = showActiveTokenId,
                    onToggleActive = { viewModel.toggleBotStatus(it) },
                    onToggleToken = { botId ->
                        showActiveTokenId = if (showActiveTokenId == botId) null else botId
                    },
                    onViewScript = { selectedBotForScriptMsg = it },
                    onDelete = { viewModel.deleteBot(it) }
                )

                // Shell Console Terminal Output
                ClusterConsoleTerminal(
                    logs = logs,
                    onClearLogs = { viewModel.clearLogs() }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Dedicated Hardware Spec Modal
        if (showSpecsModal) {
            AlertDialog(
                onDismissRequest = { showSpecsModal = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = "Server Hardware Specs",
                            tint = Color(0xFF00D4FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cluster Specifications",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Column {
                        SpecRow(icon = Icons.Default.Computer, label = "Processor Core Cluster", value = "AMD EPYC 128-Core Processor")
                        SpecRow(icon = Icons.Default.Memory, label = "Volatile ECC Memory", value = "1,024 GB DDR5 @ 4800MHz")
                        SpecRow(icon = Icons.Default.Storage, label = "NVMe Storage Array", value = "8TB Gen5 SSD RAID-10")
                        SpecRow(icon = Icons.Default.Public, label = "Location Gateway", value = "Singapore West - Zone 1")
                        SpecRow(icon = Icons.Default.Speed, label = "Uplink Speeds", value = "10.0 Gbps Burst-Ready")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showSpecsModal = false }
                    ) {
                        Text("Acknowledge", color = Color(0xFF00D4FF), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFF131322),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            )
        }

        // Script Inspector Dialog
        selectedBotForScriptMsg?.let { bot ->
            AlertDialog(
                onDismissRequest = { selectedBotForScriptMsg = null },
                title = {
                    Text(
                        text = "Script Config: ${bot.name}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = bot.script,
                            color = Color(0xFF00E676),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { selectedBotForScriptMsg = null }
                    ) {
                        Text("Close Inspector", color = Color(0xFF00D4FF))
                    }
                },
                containerColor = Color(0xFF131322),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            )
        }
    }
}

@Composable
fun InteractiveServerCard(
    selectedServer: ServerNode,
    uptime: String,
    ramUsed: Double,
    cpuUsed: Double,
    networkbySec: String,
    onSpecsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp), clip = false)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF8A2BE2).copy(alpha = 0.15f), Color(0xFF00D4FF).copy(alpha = 0.08f))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                border = BorderStroke(2.dp, Color(0xFF00D4FF).copy(alpha = 0.35f)),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onSpecsClick() }
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = "Server Grid",
                        tint = Color(0xFF00D4FF),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedServer.name,
                        color = Color(0xFF00D4FF),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                // ULTRA Badge showing pulsing glow design
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                  colors = listOf(Color(0xFFFF1744), Color(0xFFFF9100))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "1TB STALLION",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Text(
                text = "IP Source: ${selectedServer.ipAddress} | Location: ${selectedServer.location}\nDedicated AMD EPYC 128-Core system allocation node with fully isolated, high-speed secure ECC RAM pools.",
                fontSize = 12.sp,
                color = Color(0xFFCCCCCC),
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 10.dp, bottom = 18.dp)
            )

            // Dynamic Core Stats Display Grids
            val stats = listOf(
                Triple("1,024 GB RAM", String.format("%.1f GB", ramUsed), Color(0xFF00E676)),
                Triple("CPU Allocation", String.format("%.1f%%", cpuUsed), Color(0xFF00D4FF)),
                Triple("Pipeline", networkbySec, Color(0xFFFF9100)),
                Triple("Host Uptime", uptime, Color(0xFFFF1744))
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(stats[0].first, stats[0].second, stats[0].third)
                    StatCard(stats[2].first, stats[2].second, stats[2].third)
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(stats[1].first, stats[1].second, stats[1].third)
                    StatCard(stats[3].first, stats[3].second, stats[3].third)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "💡 Tap card to inspect hardware components",
                fontSize = 10.sp,
                color = Color(0xFF888899),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = label,
            color = Color(0xFF888899),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotDeploymentForm(
    botName: String,
    botType: String,
    botToken: String,
    botScript: String,
    botTypes: List<String>,
    selectedServerId: Int,
    onNameChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onTokenChange: (String) -> Unit,
    onScriptChange: (String) -> Unit,
    onDeploy: () -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.03f)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Deployment Icon",
                    tint = Color(0xFF00D4FF),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bot Deployment Console",
                    color = Color(0xFF00D4FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // Input: Name
            Text(text = "Instance Name", fontSize = 11.sp, color = Color(0xFF888899), modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = botName,
                onValueChange = onNameChange,
                placeholder = { Text("e.g. MyDiscordHelper", color = Color.White.copy(alpha = 0.3f)) },
                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00D4FF),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bot_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Choice: Bot Type Dropdown Selector
            Text(text = "Container Environment Blueprint", fontSize = 11.sp, color = Color(0xFF888899), modifier = Modifier.padding(bottom = 4.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    onClick = { expandedDropdown = !expandedDropdown },
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = botType, color = Color.White, fontSize = 13.sp)
                        Icon(
                            imageVector = if (expandedDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Select Environment",
                            tint = Color(0xFF00D4FF)
                        )
                    }
                }

                DropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color(0xFF131322))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    botTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type, color = Color.White, fontSize = 13.sp) },
                            onClick = {
                                onTypeChange(type)
                                expandedDropdown = false
                            },
                            modifier = Modifier.background(
                                if (botType == type) Color.White.copy(alpha = 0.05f) else Color.Transparent
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input: Token Credentials
            Text(text = "Security Token/Key", fontSize = 11.sp, color = Color(0xFF888899), modifier = Modifier.padding(bottom = 4.dp))
            var showTokenPassword by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = botToken,
                onValueChange = onTokenChange,
                placeholder = { Text("Enter bot API Token", color = Color.White.copy(alpha = 0.3f)) },
                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                visualTransformation = if (showTokenPassword) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00D4FF),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
                ),
                trailingIcon = {
                    IconButton(onClick = { showTokenPassword = !showTokenPassword }) {
                        Icon(
                            imageVector = if (showTokenPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Visibility",
                            tint = Color.White.copy(alpha = 0.4f)
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bot_token_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Input: Config/Script
            Text(text = "Custom Startup Script Configuration", fontSize = 11.sp, color = Color(0xFF888899), modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = botScript,
                onValueChange = onScriptChange,
                placeholder = { Text("e.g. console.log('Starting service...'); \n// Write custom terminal instructions here", color = Color.White.copy(alpha = 0.25f)) },
                textStyle = TextStyle(color = Color(0xFF00E676), fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF00E676),
                    unfocusedTextColor = Color(0xFF00E676),
                    focusedBorderColor = Color(0xFF00D4FF),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onDeploy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("deploy_bot_button")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF8A2BE2), Color(0xFF00D4FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Deploy")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Deploy to NODE #${String.format("%02d", selectedServerId)} (1TB limits)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveContainersCard(
    bots: List<BotEntity>,
    showActiveTokenId: Int?,
    onToggleActive: (BotEntity) -> Unit,
    onToggleToken: (Int) -> Unit,
    onViewScript: (BotEntity) -> Unit,
    onDelete: (BotEntity) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ViewList,
                contentDescription = "Active containers",
                tint = Color(0xFF00D4FF),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Active Docker Containers (${bots.size})",
                color = Color(0xFF00D4FF),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        if (bots.isEmpty()) {
            // Elegant Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(16.dp))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)), RoundedCornerShape(16.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = "No virtual containers",
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Active Containers Mounted",
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "All 1TB RAM limits are currently idle. Configure custom bot profiles above to initialize containers.",
                        color = Color.White.copy(alpha = 0.35f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .widthIn(max = 280.dp),
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            bots.forEach { bot ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    BotItemRow(
                        bot = bot,
                        isTokenVisible = showActiveTokenId == bot.id,
                        onToggleActive = { onToggleActive(bot) },
                        onToggleToken = { onToggleToken(bot.id) },
                        onViewScript = { onViewScript(bot) },
                        onDelete = { onDelete(bot) }
                    )
                }
            }
        }
    }
}

@Composable
fun BotItemRow(
    bot: BotEntity,
    isTokenVisible: Boolean,
    onToggleActive: () -> Unit,
    onToggleToken: () -> Unit,
    onViewScript: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (bot.isRunning) Color(0xFF00E676).copy(alpha = 0.06f) else Color.White.copy(alpha = 0.02f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = if (bot.isRunning) Color(0xFF00E676).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Info block
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (bot.isRunning) Color(0xFF00E676).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Display blueprint code glyph based on type
                        Text(
                            text = when {
                                bot.type.contains("Discord") -> "🤖"
                                bot.type.contains("Telegram") -> "📨"
                                bot.type.contains("Slack") -> "💼"
                                bot.type.contains("Scraper") -> "🕷️"
                                else -> "⚡"
                            },
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = bot.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Type: ${bot.type} | NODE #${String.format("%02d", bot.serverId)}",
                            color = Color(0xFF888899),
                            fontSize = 11.sp
                        )
                    }
                }

                // Green/Red online status indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (bot.isRunning) Color(0xFF00E676) else Color(0xFFFF1744),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (bot.isRunning) "ONLINE" else "OFFLINE",
                        color = if (bot.isRunning) Color(0xFF00E676) else Color(0xFFFF1744),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Token sub container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ACCESS TOKEN:",
                    color = Color(0xFF888899),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isTokenVisible) bot.token else "••••••••••••••••••••••••",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onToggleToken, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (isTokenVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "View Token",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Deployment Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onViewScript,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.05f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .padding(end = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = "Code", modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View Script", fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = onToggleActive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (bot.isRunning) Color(0xFFFF9100).copy(alpha = 0.15f) else Color(0xFF00E676).copy(alpha = 0.15f),
                        contentColor = if (bot.isRunning) Color(0xFFFF9100) else Color(0xFF00E676)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .padding(end = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (bot.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Status trigger",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (bot.isRunning) "Stop" else "Boot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFFF1744).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Container",
                        tint = Color(0xFFFF1744),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClusterConsoleTerminal(
    logs: List<LogEntity>,
    onClearLogs: () -> Unit
) {
    val listState = rememberLazyListState()

    // Keep console scrolled to the latest statements
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Live output console",
                    tint = Color(0xFF00D4FF),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Live Cluster SSH Terminal",
                    color = Color(0xFF00D4FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            TextButton(
                onClick = onClearLogs,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ClearAll,
                    contentDescription = "Clear logs",
                    tint = Color(0xFF888899),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear Panel", color = Color(0xFF888899), fontSize = 11.sp)
            }
        }

        // Monospace Console Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(logs) { log ->
                    Text(
                        text = log.message,
                        color = when (log.type) {
                            "success" -> Color(0xFF00E676)
                            "warning" -> Color(0xFFFF9100)
                            "error" -> Color(0xFFFF1744)
                            "info" -> Color(0xFF00D4FF)
                            else -> Color.White
                        },
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SpecRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8A2BE2),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, color = Color(0xFF888899), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
fun ServerClusterGrid(
    servers: List<ServerNode>,
    selectedServerId: Int,
    bots: List<BotEntity>,
    serversLiveStats: Map<Int, ServerLiveStats>,
    onServerSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Dns,
                contentDescription = "Server Grid",
                tint = Color(0xFF00D4FF),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "20 TB Live Concurrent VPS Cluster",
                    color = Color(0xFF00D4FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Status: ALL 20 NODES ONLINE & SENSING | 20,480 GB Total Capacity",
                    color = Color(0xFF00E676),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        val rows = remember(servers) { servers.chunked(4) }
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEach { rowServers ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowServers.forEach { server ->
                        val isSelected = server.id == selectedServerId
                        val activeBotsOnNode = bots.count { it.serverId == server.id && it.isRunning }
                        val stats = serversLiveStats[server.id]
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp)
                                .background(
                                    if (isSelected) Color(0xFF00D4FF).copy(alpha = 0.12f)
                                    else Color.Black.copy(alpha = 0.4f),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF00D4FF) else Color.White.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onServerSelect(server.id) }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Node Title Line
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Node ${String.format("%02d", server.id)}",
                                        color = if (isSelected) Color(0xFF00D4FF) else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(
                                                if (activeBotsOnNode > 0) Color(0xFF00E676) else Color(0xFFFF9100),
                                                CircleShape
                                            )
                                    )
                                }

                                // Live dynamic CPU load
                                val liveCpu = stats?.cpuUsedPercent ?: 0.5
                                val cpuText = String.format("%.1f%%", liveCpu)
                                Text(
                                    text = "$cpuText CPU",
                                    color = if (isSelected) Color(0xFF00D4FF) else if (activeBotsOnNode > 0) Color(0xFF00E676) else Color(0xFF888899),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace
                                )

                                // Live dynamic RAM load
                                val liveRam = stats?.ramUsedGb ?: 14.5
                                val ramText = if (liveRam >= 100.0) {
                                    String.format("%.0f", liveRam)
                                } else {
                                    String.format("%.1f", liveRam)
                                }
                                Text(
                                    text = "${ramText}G / 1T",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
