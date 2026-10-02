package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun RoastHeader(
    isDemoMode: Boolean,
    modifier: Modifier = Modifier
) {
    var showDemoDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(PurplePrimary, OrangeFlame)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "😂",
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "AI Roast Generator",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite,
                        modifier = Modifier.testTag("app_title")
                    )
                    Text(
                        text = "Give me a target. I'll bring the jokes.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            if (isDemoMode) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonYellow.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonYellow.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clickable { showDemoDialog = true }
                        .testTag("demo_mode_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ Demo Mode",
                            color = NeonYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showDemoDialog) {
        AlertDialog(
            onDismissRequest = { showDemoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡ Demo Mode Active", fontWeight = FontWeight.Bold, color = TextWhite)
                }
            },
            text = {
                Column {
                    Text(
                        text = "The app is currently generating curated, hilarious offline roasts in English, Tamil, and Tanglish so you can test all features immediately without requiring an API key.\n\nTo connect live Google Gemini AI, add your GEMINI_API_KEY in the AI Studio Secrets panel!",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDemoDialog = false }) {
                    Text("Got It!", color = PurplePrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
