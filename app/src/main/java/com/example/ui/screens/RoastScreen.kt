package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoastLanguage
import com.example.data.model.RoastLevel
import com.example.data.model.RoastStyle
import com.example.ui.RoastUiState
import com.example.ui.components.RoastCookingCard
import com.example.ui.components.RoastInputSection
import com.example.ui.components.RoastResultCard
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.HotRed
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun RoastScreen(
    targetName: String,
    description: String,
    selectedLanguage: RoastLanguage,
    selectedLevel: RoastLevel,
    selectedStyle: RoastStyle,
    uiState: RoastUiState,
    onTargetNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLanguageChange: (RoastLanguage) -> Unit,
    onLevelChange: (RoastLevel) -> Unit,
    onStyleChange: (RoastStyle) -> Unit,
    onRoastClick: () -> Unit,
    onRandomClick: () -> Unit,
    onSelectVariation: (Int) -> Unit,
    onSaveRoast: () -> Unit,
    onRoastAgain: () -> Unit,
    onOpenLiveVoice: () -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("roast_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Voice Callout Card (gemini-3.8-live)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(
                            PurplePrimary.copy(alpha = 0.25f),
                            OrangeFlame.copy(alpha = 0.25f)
                        )
                    )
                )
                .border(
                    1.dp,
                    androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(PurplePrimary, OrangeFlame)),
                    RoundedCornerShape(18.dp)
                )
                .clickable { onOpenLiveVoice() }
                .padding(14.dp)
                .testTag("open_live_voice_banner")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = "🎙️", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Live Voice Roast Battle",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = HotRed.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "gemini-3.8-live",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangeFlame,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Talk to the AI in real-time & get roasted live!",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }
                }

                Text(
                    text = "START ➔",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = OrangeFlame
                )
            }
        }
        // Hero Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ready to Get Roasted? 😂",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Drop a name, situation, or description. Let AI cook the roast.",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }

        // Animated State Switching
        when (uiState) {
            is RoastUiState.Idle -> {
                RoastInputSection(
                    targetName = targetName,
                    description = description,
                    selectedLanguage = selectedLanguage,
                    selectedLevel = selectedLevel,
                    selectedStyle = selectedStyle,
                    onTargetNameChange = onTargetNameChange,
                    onDescriptionChange = onDescriptionChange,
                    onLanguageChange = onLanguageChange,
                    onLevelChange = onLevelChange,
                    onStyleChange = onStyleChange,
                    onRoastClick = onRoastClick,
                    onRandomClick = onRandomClick
                )
            }

            is RoastUiState.Cooking -> {
                RoastCookingCard(statusMessage = uiState.stepMessage)
            }

            is RoastUiState.Success -> {
                RoastResultCard(
                    result = uiState.result,
                    currentIndex = uiState.currentIndex,
                    isSaved = uiState.isSaved,
                    onSelectIndex = onSelectVariation,
                    onCopy = onCopy,
                    onShare = onShare,
                    onSave = onSaveRoast,
                    onRoastAgain = onRoastAgain,
                    onGenerateMore = onRoastClick
                )
            }

            is RoastUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, HotRed.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = HotRed,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Oops! Roast Kitchen Alert",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.message,
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDismissError,
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
