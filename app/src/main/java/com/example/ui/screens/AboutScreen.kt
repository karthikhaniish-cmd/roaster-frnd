package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.ToxicGreen

@Composable
fun AboutScreen(
    isDemoMode: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("about_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(PurplePrimary.copy(alpha = 0.2f), OrangeFlame.copy(alpha = 0.2f))
                    )
                )
                .border(1.5.dp, Brush.linearGradient(listOf(PurplePrimary, OrangeFlame)), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "😂", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI Roast Generator",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                        Text(
                            text = "Playful banter for friends & squads",
                            fontSize = 13.sp,
                            color = OrangeFlame,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Built with Google Gemini 3.5 Flash to generate witty, lighthearted roasts on harmless everyday habits, procrastination, gaming, bad Wi-Fi, and college life.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = TextMuted
                )
            }
        }

        // Live Voice API with gemini-3.8-live
        AboutCard(
            icon = Icons.Default.Code,
            iconTint = PurpleLight,
            title = "🎙️ Real-Time Voice (gemini-3.8-live)",
            description = "Experience lightning-fast verbal roast battles powered by the gemini-3.8-live Live API! Speak naturally into your microphone or type quick comebacks, and the AI speaks back hilarious roasts in real-time with full English, Tamil, and Tanglish banter."
        )

        // Safety Section (CRITICAL MANDATE)
        AboutCard(
            icon = Icons.Default.Security,
            iconTint = ToxicGreen,
            title = "🛡️ Safety First Policy",
            description = "This app strictly produces playful entertainment and banter — never real harassment. We never generate threats, hate speech, slurs, sexual content, personal doxxing, or attacks on protected characteristics. All humor focuses entirely on harmless quirks!"
        )

        // Tanglish & Tamil Section
        AboutCard(
            icon = Icons.Default.Translate,
            iconTint = OrangeFlame,
            title = "🔥 Tamil & Tanglish Mastery",
            description = "Natural understanding of Tamil written in English characters (Tanglish) and native Tamil script. Sounds like authentic college campus banter with friends: 'Dei', 'Machi', 'scene podra', 'attendance percentage', and 'vibe check'!"
        )

        // Harmless Targets
        AboutCard(
            icon = Icons.Default.Info,
            iconTint = NeonYellow,
            title = "🎯 Top Harmless Targets",
            description = "• Procrastination & 3 AM motivation\n• Snooze button addictions & sleep cycles\n• 5 hours of gaming with 200ms ping excuses\n• Wi-Fi that only works for ads\n• Taking 300 selfies for one Instagram story\n• Claiming 'I am 5 mins away' from under the blanket"
        )

        // Gemini Setup & Secrets
        AboutCard(
            icon = Icons.Default.Key,
            iconTint = PurpleLight,
            title = if (isDemoMode) "⚡ Running on Demo Mode" else "✨ Connected to Gemini 3.5 Flash",
            description = if (isDemoMode)
                "Demo Mode is currently active with pre-loaded comedy roasts so you can test everything right now! To enable live Gemini AI generation, provide your GEMINI_API_KEY in the AI Studio Secrets panel."
            else
                "Your GEMINI_API_KEY is active! Live AI requests are being streamed directly through the Gemini 3.5 Flash model."
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AboutCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = description,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = TextMuted
        )
    }
}
