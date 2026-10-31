package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MediaClip
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.InstagramGradient
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ReelMagenta
import com.example.ui.theme.ReelOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ClipTimelineView(
    clips: List<MediaClip>,
    currentClipIndex: Int,
    bpm: Int,
    totalDurationSeconds: Float,
    onClipSelected: (Int) -> Unit,
    onDurationChanged: (clipId: String, duration: Float) -> Unit,
    onRemoveClip: (clipId: String) -> Unit,
    onAddMediaClick: () -> Unit,
    onAutoBeatMatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // Header with total duration and Auto-Beat Match button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Clip Timeline (${clips.size} clips)",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total Duration: ${"%.1f".format(totalDurationSeconds)}s • $bpm BPM",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            // Auto Beat Match Button
            Surface(
                onClick = onAutoBeatMatchClick,
                shape = RoundedCornerShape(20.dp),
                color = ReelMagenta.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ReelMagenta),
                modifier = Modifier.testTag("auto_beat_match_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Auto Beat Match",
                        tint = ReelOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Auto Beat Sync",
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Clips Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(clips) { index, clip ->
                val isSelected = (index == currentClipIndex)
                Box(
                    modifier = Modifier
                        .width(76.dp)
                        .height(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) ReelMagenta else DarkCardBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onClipSelected(index) }
                ) {
                    // Clip thumbnail
                    if (clip.uriString != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(clip.uriString)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Clip",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (clip.drawableResId != null) {
                        Image(
                            painter = painterResource(id = clip.drawableResId),
                            contentDescription = "Clip",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Delete button
                    if (clips.size > 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.65f))
                                .clickable { onRemoveClip(clip.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete Clip",
                                tint = TextWhite,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    // Duration badge & duration adjustment
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${"%.1f".format(clip.durationSeconds)}s",
                                color = if (isSelected) NeonCyan else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Add (+) Media button
            item {
                Box(
                    modifier = Modifier
                        .width(68.dp)
                        .height(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                        .clickable { onAddMediaClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(InstagramGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Media",
                                tint = TextWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Active clip duration adjuster
        val activeClip = clips.getOrNull(currentClipIndex)
        if (activeClip != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Adjust Clip #${currentClipIndex + 1} Timing:",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = {
                            val newDur = (activeClip.durationSeconds - 0.2f).coerceAtLeast(0.5f)
                            onDurationChanged(activeClip.id, newDur)
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Text(
                            text = "- 0.2s",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "${"%.1f".format(activeClip.durationSeconds)}s",
                        color = ReelMagenta,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        onClick = {
                            val newDur = (activeClip.durationSeconds + 0.2f).coerceAtMost(5.0f)
                            onDurationChanged(activeClip.id, newDur)
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Text(
                            text = "+ 0.2s",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
