package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.RoastLanguage
import com.example.ui.LiveRoastMessage
import com.example.ui.MessageRole
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.HotRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.theme.ToxicGreen

@Composable
fun LiveRoastScreen(
    messages: List<LiveRoastMessage>,
    isListening: Boolean,
    isSpeaking: Boolean,
    isProcessing: Boolean,
    audioLevel: Float,
    liveTranscript: String,
    isMuted: Boolean,
    selectedLanguage: RoastLanguage,
    quickPrompts: List<String>,
    liveError: String?,
    onToggleListening: () -> Unit,
    onSendMessage: (String) -> Unit,
    onToggleMute: () -> Unit,
    onReplayVoice: (String) -> Unit,
    onClearBattle: () -> Unit,
    onDismissError: () -> Unit,
    onLanguageChange: (RoastLanguage) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Permission launcher for microphone
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onToggleListening()
        }
    }

    val handleMicClick = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            onToggleListening()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Auto-scroll to bottom on new messages
    LaunchedEffect(messages.size, liveTranscript) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Pulsing animation for microphone and audio waves
    val infiniteTransition = rememberInfiniteTransition(label = "live_waves")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("live_roast_screen")
    ) {
        // Top Live Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Live status dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isListening || isSpeaking || isProcessing) HotRed else ToxicGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "🎙️ Live Roast Battle",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                    Text(
                        text = "Model: gemini-3.8-live",
                        fontSize = 11.sp,
                        color = PurpleLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language switcher
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.clickable {
                        val next = when (selectedLanguage) {
                            RoastLanguage.TANGLISH -> RoastLanguage.ENGLISH
                            RoastLanguage.ENGLISH -> RoastLanguage.TAMIL
                            RoastLanguage.TAMIL -> RoastLanguage.TANGLISH
                        }
                        onLanguageChange(next)
                    }
                ) {
                    Text(
                        text = selectedLanguage.badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Mute / Unmute
                IconButton(onClick = onToggleMute, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = if (isMuted) "Unmute" else "Mute",
                        tint = if (isMuted) HotRed else NeonYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Clear
                IconButton(onClick = onClearBattle, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear",
                        tint = TextSubtle,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Live Audio Visualizer Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(
                    1.dp,
                    if (isListening) HotRed.copy(alpha = 0.6f) else if (isSpeaking) OrangeFlame.copy(alpha = 0.6f) else DarkBorder,
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Dynamic Audio Bars
                val activeWave = isListening || isSpeaking
                val dynamicHeights = listOf(
                    if (activeWave) (12 + audioLevel * 24).dp else 8.dp,
                    if (activeWave) (18 + audioLevel * 32).dp else 14.dp,
                    if (activeWave) (24 + audioLevel * 40).dp else 20.dp,
                    if (activeWave) (16 + audioLevel * 28).dp else 12.dp,
                    if (activeWave) (10 + audioLevel * 18).dp else 6.dp
                )

                dynamicHeights.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(h)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isListening) HotRed else if (isSpeaking) OrangeFlame else PurplePrimary.copy(alpha = 0.4f)
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = when {
                        isProcessing -> "🔥 AI is crafting a live burn..."
                        isListening -> "👂 Listening to you... Speak now!"
                        isSpeaking -> "🔊 AI is roasting out loud!"
                        else -> "Tap the Mic or a prompt to start roasting"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isListening -> HotRed
                        isSpeaking -> OrangeFlame
                        isProcessing -> NeonYellow
                        else -> TextMuted
                    }
                )
            }
        }

        // Live partial transcript hint
        if (liveTranscript.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF221133))
                    .padding(8.dp)
            ) {
                Text(
                    text = "“$liveTranscript...”",
                    fontSize = 12.sp,
                    color = PurpleLight,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }

        // Error message if mic unavailable
        if (liveError != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(HotRed.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = liveError, fontSize = 11.sp, color = HotRed, modifier = Modifier.weight(1f))
                Text(
                    text = "Dismiss",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    modifier = Modifier.clickable { onDismissError() }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Live Chat Messages Feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                LiveChatBubble(
                    message = msg,
                    onReplayVoice = { onReplayVoice(msg.text) },
                    onCopy = { onCopy(msg.text) }
                )
            }

            if (isProcessing) {
                item {
                    Row(
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = OrangeFlame,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Roastmaster is thinking of a burn...",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Quick Banter Prompts
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.35f)),
                    modifier = Modifier.clickable { onSendMessage(prompt) }
                ) {
                    Text(
                        text = prompt,
                        fontSize = 11.5.sp,
                        color = TextWhite,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Bottom Controls: Glowing Mic Button + Text Input fallback
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Text Input
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Speak or type a roast challenge...", fontSize = 13.sp, color = TextSubtle) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("live_chat_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                )
            )

            // Send text button if user typed
            if (textInput.isNotBlank()) {
                IconButton(
                    onClick = {
                        val text = textInput
                        textInput = ""
                        onSendMessage(text)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Big Live Microphone Button
            val micScale = if (isListening) pulseScale else 1.0f
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .scale(micScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            if (isListening) listOf(HotRed, OrangeFlame) else listOf(OrangeFlame, PurplePrimary)
                        )
                    )
                    .clickable { handleMicClick() }
                    .testTag("live_mic_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = if (isListening) "Stop Listening" else "Start Speaking",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun LiveChatBubble(
    message: LiveRoastMessage,
    onReplayVoice: () -> Unit,
    onCopy: () -> Unit
) {
    val isUser = message.role == MessageRole.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(PurplePrimary, OrangeFlame))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "😂", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) PurplePrimary.copy(alpha = 0.28f) else DarkSurfaceElevated
                )
                .border(
                    1.dp,
                    if (isUser) PurplePrimary else OrangeFlame.copy(alpha = 0.4f),
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(0.82f)
        ) {
            Text(
                text = if (isUser) "You" else "AI Roastmaster (Live)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUser) PurpleLight else OrangeFlame
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.text,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = TextWhite
            )

            if (!isUser) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onReplayVoice, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Again",
                            tint = NeonYellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2C2248)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "😎", fontSize = 16.sp)
            }
        }
    }
}
