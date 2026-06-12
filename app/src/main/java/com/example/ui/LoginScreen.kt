package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: DashboardViewModel,
    onLoginSuccess: (String) -> Unit
) {
    var showAccountPicker by remember { mutableStateOf(false) }
    var isAuthenticating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Futuristic space backdrop with glowing background orbs
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07070F))
            .drawBehind {
                // Violet active orb (top-left)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF8A2BE2).copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.8f
                    )
                )
                // Cyan active orb (bottom-right)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00D4FF).copy(alpha = 0.2f), Color.Transparent),
                        center = Offset(size.width, size.height),
                        radius = size.width * 0.8f
                    )
                )
                // Red active orb (mid-left)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFF1744).copy(alpha = 0.1f), Color.Transparent),
                        center = Offset(0f, size.height * 0.5f),
                        radius = size.width * 0.5f
                    )
                )
            }
            .padding(24.dp)
            .navigationBarsPadding()
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
        ) {
            // Elegant glowing launcher logo
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(24.dp), clip = false)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF8A2BE2), Color(0xFF00D4FF))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = "Server Logo",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "ULTRABOT",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                style = LocalTextStyle.current.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF8A2BE2), Color(0xFF00D4FF))
                    )
                )
            )

            Text(
                text = "1TB DEDICATED HOSTING CLUSTER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF888899),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // Glassmorphic main login box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.02f))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(
                        Color.White.copy(alpha = 0.03f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(28.dp)
            ) {
                Column {
                    Text(
                        text = "Access Server Panel",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Authorize client shell terminal to register bots & monitor RAM clusters",
                        fontSize = 13.sp,
                        color = Color(0xFFAAAAAA),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 28.dp)
                    )

                    if (isAuthenticating) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF00D4FF),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Establishing secure shell tunnel...",
                                fontSize = 12.sp,
                                color = Color(0xFF8A2BE2),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        // Google login button
                        Button(
                            onClick = { showAccountPicker = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF1E1E24)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("google_login_button"),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Simple mock Google icon using Canvas to represent colors
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .drawBehind {
                                            drawCircle(color = Color(0xFF4285F4)) // blue
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "G",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Google",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Divider(color = Color.White.copy(alpha = 0.08f))

                    Spacer(modifier = Modifier.height(20.dp))

                    // Feature highlights
                    FeatureItem(text = "1,024 GB High-Freq RAM Allocation")
                    FeatureItem(text = "Isolated Core VPS Isolation")
                    FeatureItem(text = "10 Gbps Ultra-low Latency Pipelines")
                    FeatureItem(text = "Singapore Zone 1 Mainframe Uplink")
                }
            }
        }

        // Account picker sheet simulator
        AnimatedVisibility(
            visible = showAccountPicker,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showAccountPicker = false }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF131322)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 380.dp)
                            .clickable(enabled = false) {}
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "Choose Google Account",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Simulated azizulkan6@gmail.com account block
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .clickable {
                                        showAccountPicker = false
                                        isAuthenticating = true
                                        scope.launch {
                                            delay(1500) // simulated loading speed
                                            isAuthenticating = false
                                            viewModel.login("azizulkan6@gmail.com")
                                            onLoginSuccess("azizulkan6@gmail.com")
                                        }
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color(0xFF8A2BE2), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "AK",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Azizul Kan",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Developer Verified",
                                            tint = Color(0xFF00E676),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = "azizulkan6@gmail.com",
                                        color = Color(0xFF888899),
                                        fontSize = 12.sp
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Sign in",
                                    tint = Color.White.copy(alpha = 0.4f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "To continue, UltraBot will share your name, email address, profile picture and register cluster endpoints on Singapore mainframe hosts.",
                                fontSize = 11.sp,
                                color = Color(0xFF888899),
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            TextButton(
                                onClick = { showAccountPicker = false },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Cancel", color = Color(0xFF00D4FF))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = "Check",
            tint = Color(0xFF00E676),
            modifier = Modifier.size(8.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFFCCCCCC)
        )
    }
}
