package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.RoastEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.HotRed
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
    allRoasts: List<RoastEntity>,
    favoriteRoasts: List<RoastEntity>,
    onToggleFavorite: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOnlyFavorites by remember { mutableStateOf(false) }
    val displayList = if (showOnlyFavorites) favoriteRoasts else allRoasts
    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("history_screen")
    ) {
        // Filter bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !showOnlyFavorites,
                onClick = { showOnlyFavorites = false },
                label = { Text("All Roasts (${allRoasts.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PurplePrimary.copy(alpha = 0.25f),
                    selectedLabelColor = TextWhite,
                    containerColor = DarkSurfaceElevated,
                    labelColor = TextMuted
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = !showOnlyFavorites,
                    borderColor = if (!showOnlyFavorites) PurplePrimary else DarkBorder
                )
            )

            FilterChip(
                selected = showOnlyFavorites,
                onClick = { showOnlyFavorites = true },
                label = { Text("Favorites ❤️ (${favoriteRoasts.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = HotRed.copy(alpha = 0.25f),
                    selectedLabelColor = TextWhite,
                    containerColor = DarkSurfaceElevated,
                    labelColor = TextMuted
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = showOnlyFavorites,
                    borderColor = if (showOnlyFavorites) HotRed else DarkBorder
                )
            )
        }

        if (displayList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (showOnlyFavorites) "💔" else "📜", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (showOnlyFavorites) "No favorite roasts yet" else "No roasts generated yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (showOnlyFavorites) "Tap the heart icon on any roast to save it here!" else "Head over to the Roast tab and start cooking! 😂",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
            ) {
                items(displayList, key = { it.id }) { roast ->
                    RoastHistoryItem(
                        roast = roast,
                        formattedDate = dateFormatter.format(Date(roast.timestamp)),
                        onToggleFavorite = { onToggleFavorite(roast.id) },
                        onDelete = { onDelete(roast.id) },
                        onCopy = { onCopy(roast.roastText) },
                        onShare = { onShare(roast.roastText) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoastHistoryItem(
    roast: RoastEntity,
    formattedDate: String,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "😂 ${roast.targetName}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = TextSubtle
                )
            }

            Row {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (roast.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (roast.isFavorite) HotRed else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TextSubtle,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tags
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = OrangeFlame.copy(alpha = 0.15f)
            ) {
                Text(
                    text = roast.level,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeFlame,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = PurplePrimary.copy(alpha = 0.15f)
            ) {
                Text(
                    text = roast.language,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurpleLight,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = roast.roastText,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = TextWhite
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = OrangeFlame,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = PurplePrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
