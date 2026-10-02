package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.RoastLanguage
import com.example.data.model.RoastLevel
import com.example.data.model.RoastStyle
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.HotRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RoastInputSection(
    targetName: String,
    description: String,
    selectedLanguage: RoastLanguage,
    selectedLevel: RoastLevel,
    selectedStyle: RoastStyle,
    onTargetNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLanguageChange: (RoastLanguage) -> Unit,
    onLevelChange: (RoastLevel) -> Unit,
    onStyleChange: (RoastStyle) -> Unit,
    onRoastClick: () -> Unit,
    onRandomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var styleDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .testTag("roast_input_card")
    ) {
        // Section title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🎯 Configure The Roast",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "Pick language, spicy level & target details",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            // Quick Random Button
            OutlinedButton(
                onClick = onRandomClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonYellow
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonYellow.copy(alpha = 0.5f)),
                modifier = Modifier.testTag("random_roast_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = "Random",
                    modifier = Modifier.size(16.dp),
                    tint = NeonYellow
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("🎲 Random", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 1. Target Name
        Text(
            text = "Target Name / Nickname",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = targetName,
            onValueChange = onTargetNameChange,
            placeholder = { Text("e.g. Harish", color = TextSubtle) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("target_name_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = Color(0xFF0F0B1C),
                unfocusedContainerColor = Color(0xFF0F0B1C)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Tell us about them
        Text(
            text = "Tell us about them / Situation",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = {
                Text(
                    "Always late to class, thinks he's a great dancer, spends 5 hours gaming…",
                    color = TextSubtle,
                    fontSize = 13.sp
                )
            },
            minLines = 3,
            maxLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("description_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = Color(0xFF0F0B1C),
                unfocusedContainerColor = Color(0xFF0F0B1C)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Language Selector
        Text(
            text = "Roast Language",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoastLanguage.values().forEach { lang ->
                val isSelected = selectedLanguage == lang
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) PurplePrimary.copy(alpha = 0.25f) else Color(0xFF0F0B1C)
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) PurplePrimary else DarkBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onLanguageChange(lang) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = lang.badge,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextWhite else TextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Roast Level Cards
        Text(
            text = "Roast Level",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoastLevel.values().forEach { level ->
                val isSelected = selectedLevel == level
                val accentColor = when (level) {
                    RoastLevel.FRIENDLY -> OrangeFlame
                    RoastLevel.SAVAGE -> HotRed
                    RoastLevel.NUCLEAR -> NeonYellow
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.18f) else Color(0xFF0F0B1C)
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) accentColor else DarkBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onLevelChange(level) }
                        .padding(vertical = 10.dp, horizontal = 6.dp)
                        .testTag("level_${level.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${level.emoji} ${level.label}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) TextWhite else TextMuted
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = level.subtitle,
                            fontSize = 9.5.sp,
                            color = if (isSelected) accentColor else TextSubtle,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Roast Style
        Text(
            text = "Roast Style",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = styleDropdownExpanded,
            onExpandedChange = { styleDropdownExpanded = !styleDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "${selectedStyle.emoji} ${selectedStyle.displayName}",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = styleDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .testTag("style_dropdown"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = Color(0xFF0F0B1C),
                    unfocusedContainerColor = Color(0xFF0F0B1C)
                )
            )

            ExposedDropdownMenu(
                expanded = styleDropdownExpanded,
                onDismissRequest = { styleDropdownExpanded = false },
                modifier = Modifier.background(DarkSurfaceElevated)
            ) {
                RoastStyle.values().forEach { style ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${style.emoji} ${style.displayName}",
                                color = TextWhite,
                                fontWeight = if (style == selectedStyle) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onStyleChange(style)
                            styleDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔥 ROAST ME Button (Large Gen-Z Gradient Button)
        Button(
            onClick = onRoastClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("roast_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                PurplePrimary,
                                OrangeFlame
                            )
                        ),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Flame",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔥 ROAST ME",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
