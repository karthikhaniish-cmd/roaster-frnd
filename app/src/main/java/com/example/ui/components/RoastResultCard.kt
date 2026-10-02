package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoastResult
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.HotRed
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.PurpleDark
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoastResultCard(
    result: RoastResult,
    currentIndex: Int,
    isSaved: Boolean,
    onSelectIndex: (Int) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onSave: () -> Unit,
    onRoastAgain: () -> Unit,
    onGenerateMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRoastText = result.roasts.getOrElse(currentIndex) { "" }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(500)) + scaleIn(tween(500), initialScale = 0.95f)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurfaceElevated)
                .border(
                    1.5.dp,
                    Brush.linearGradient(
                        listOf(PurplePrimary, OrangeFlame)
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
                .testTag("roast_result_card")
        ) {
            // Header: 😂 TARGET GETS ROASTED
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "😂",
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${result.targetName.uppercase()} GETS ROASTED",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite,
                        maxLines = 1
                    )
                }

                // Variation count pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PurplePrimary.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "${currentIndex + 1}/${result.roasts.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata Tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OrangeFlame.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${result.level.emoji} ${result.level.label}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeFlame,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PurplePrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${result.language.badge}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "${result.style.emoji} ${result.style.displayName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Roast Text Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0B1C))
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "“",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = OrangeFlame.copy(alpha = 0.6f),
                        lineHeight = 24.sp
                    )

                    Text(
                        text = currentRoastText,
                        fontSize = 17.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("roast_text_display")
                    )

                    Text(
                        text = "”",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = OrangeFlame.copy(alpha = 0.6f),
                        lineHeight = 24.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Variations Switcher Chips (Variation 1, 2, 3)
            if (result.roasts.size > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    result.roasts.forEachIndexed { idx, _ ->
                        val isSelected = idx == currentIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) PurplePrimary.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) PurplePrimary else DarkBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectIndex(idx) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Roast ${idx + 1}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextWhite else TextMuted
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Primary Action Buttons: Copy, Share, Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onCopy(currentRoastText) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("copy_roast_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp),
                        tint = OrangeFlame
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { onShare(currentRoastText) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_roast_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(16.dp),
                        tint = PurplePrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onSave,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_roast_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isSaved) HotRed else TextWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSaved) HotRed else DarkBorder)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save",
                        modifier = Modifier.size(16.dp),
                        tint = if (isSaved) HotRed else TextMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isSaved) "Saved" else "Save", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Buttons: Roast Again & Generate 3 More Roasts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRoastAgain,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("roast_again_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleDark
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Roast Again",
                        modifier = Modifier.size(16.dp),
                        tint = PurpleLight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🔥 Roast Again", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }

                Button(
                    onClick = onGenerateMore,
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("generate_more_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeFlame
                    )
                ) {
                    Text("⚡ Generate 3 More", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }
    }
}
