package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewCarousel
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ReelEntity
import com.example.data.model.DefaultTemplates
import com.example.data.model.ReelTemplate
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.InstagramGradient
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ProGold
import com.example.ui.theme.ProGoldGradient
import com.example.ui.theme.ReelMagenta
import com.example.ui.theme.ReelOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun HomeScreen(
    isPro: Boolean,
    savedReels: List<ReelEntity>,
    onOpenEditor: () -> Unit,
    onMediaSelected: (List<String>) -> Unit,
    onLoadSampleStock: () -> Unit,
    onTemplateSelect: (ReelTemplate) -> Unit,
    onOpenProModal: () -> Unit,
    onOpenTemplatesTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            onMediaSelected(uris.map { it.toString() })
            onOpenEditor()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // --- TOP BAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(InstagramGradient)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(DarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = ReelMagenta,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "AI Reel Maker",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Instagram Beat Sync Studio",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Surface(
                onClick = onOpenProModal,
                shape = RoundedCornerShape(18.dp),
                color = if (isPro) ProGold.copy(alpha = 0.2f) else DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isPro) ProGold else Color(0xFF383848)
                ),
                modifier = Modifier.testTag("home_pro_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ProGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPro) "PRO ACTIVE" else "GET PRO",
                        color = ProGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- HERO ACTION CARD ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2C1032),
                            Color(0xFF1B1428),
                            Color(0xFF16161F)
                        )
                    )
                )
                .border(1.5.dp, Color(0xFF452055), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ReelMagenta,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "AI POWERED",
                            color = TextWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Auto Beat-Sync & Captions",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Turn Photos & Videos\nInto Viral Instagram Reels",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Upload media, and AI auto-syncs cuts to the music drop, crafts animated karaoke captions, and adds cinematic transitions.",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Upload Media Button
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(InstagramGradient)
                            .testTag("upload_media_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Upload Media",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Stock Clips Sample Button
                    Surface(
                        onClick = {
                            onLoadSampleStock()
                            onOpenEditor()
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383848)),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("try_stock_clips_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Try Demo Reel",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- TRENDING TEMPLATES CAROUSEL ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Trending Reel Templates",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ready-made beat recipes",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Surface(
                onClick = onOpenTemplatesTab,
                color = Color.Transparent
            ) {
                Text(
                    text = "See All >",
                    color = ReelOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DefaultTemplates.templates) { tmpl ->
                val isLocked = tmpl.isPro && !isPro

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .width(160.dp)
                        .clickable {
                            if (isLocked) {
                                onOpenProModal()
                            } else {
                                onTemplateSelect(tmpl)
                                onOpenEditor()
                            }
                        }
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        ) {
                            Image(
                                painter = painterResource(id = tmpl.previewImageRes),
                                contentDescription = tmpl.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Black.copy(alpha = 0.3f),
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.75f)
                                            )
                                        )
                                    )
                            )

                            // Tag
                            Surface(
                                color = if (tmpl.isPro) ProGold else ReelMagenta,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = tmpl.tagLabel,
                                    color = if (tmpl.isPro) Color.Black else TextWhite,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (isLocked) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Pro",
                                        tint = ProGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            // BPM
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${tmpl.musicTrack.bpm} BPM",
                                    color = TextWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = tmpl.title,
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = tmpl.category,
                                color = TextMuted,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- CREATOR FEATURES BANNER ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkCard)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Engine Features",
                color = TextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            FeatureItem(
                icon = Icons.Default.MusicNote,
                title = "Live Audio Beat-Drop Synthesizer",
                subtitle = "Hardware synchronized PCM sound wave in real-time"
            )
            FeatureItem(
                icon = Icons.Default.AutoAwesome,
                title = "Gemini 3.5 Viral Script AI",
                subtitle = "High-retention hooks, dynamic captions & hashtags"
            )
            FeatureItem(
                icon = Icons.Default.ElectricBolt,
                title = "6 Dynamic Transitions",
                subtitle = "Zoom pulse, strobe flash, cyberpunk glitch, whip pan"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- MY SAVED REELS SECTION ---
        if (savedReels.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Reels (${savedReels.size})",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            savedReels.take(3).forEach { reel ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOpenEditor() }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp, 60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                        ) {
                            Image(
                                painter = painterResource(id = if (reel.thumbnailResId != 0) reel.thumbnailResId else R.drawable.sample_travel),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = reel.title,
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${reel.hookText} • ${"%.1f".format(reel.durationSeconds)}s",
                                color = TextMuted,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Open",
                            tint = ReelOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ReelMagenta.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ReelOrange,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
