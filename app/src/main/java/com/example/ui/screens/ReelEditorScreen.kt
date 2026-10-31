package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AiHookGeneratorDialog
import com.example.ui.components.CaptionStyleSelector
import com.example.ui.components.ClipTimelineView
import com.example.ui.components.ExportDialog
import com.example.ui.components.MusicSelectorSheet
import com.example.ui.components.ReelPlayerView
import com.example.ui.components.TransitionSelector
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.InstagramGradient
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ProGold
import com.example.ui.theme.ReelMagenta
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.EditorBottomTab
import com.example.ui.viewmodel.ReelUiState
import com.example.ui.viewmodel.ReelViewModel

@Composable
fun ReelEditorScreen(
    viewModel: ReelViewModel,
    uiState: ReelUiState,
    onNavigateBack: () -> Unit,
    onOpenProModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAiDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    // Android Photo Picker for picking multiple photos or videos
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addClipsFromUris(uris.map { it.toString() })
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // --- 1. TOP HEADER BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = uiState.reelTitle,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "Instagram Reel 9:16",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Pro Badge Button
                Surface(
                    onClick = onOpenProModal,
                    shape = RoundedCornerShape(16.dp),
                    color = if (uiState.isProSubscriber) ProGold.copy(alpha = 0.2f) else DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (uiState.isProSubscriber) ProGold else Color(0xFF383848)
                    ),
                    modifier = Modifier.testTag("pro_badge_header")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = ProGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (uiState.isProSubscriber) "PRO ACTIVE" else "PRO",
                            color = ProGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Export Button
                Button(
                    onClick = { showExportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(InstagramGradient)
                        .testTag("export_header_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Export",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 2. SCROLLABLE REEL PREVIEW & EDIT TOOLS ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp)
        ) {
            // Central 9:16 Reel Player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(390.dp),
                contentAlignment = Alignment.Center
            ) {
                ReelPlayerView(
                    clips = uiState.clips,
                    currentClipIndex = uiState.currentClipIndex,
                    playbackPositionSeconds = uiState.playbackPositionSeconds,
                    totalDurationSeconds = uiState.totalDurationSeconds,
                    isPlaying = uiState.isPlaying,
                    isMuted = uiState.isMuted,
                    selectedTrack = uiState.selectedTrack,
                    selectedTransition = uiState.selectedTransition,
                    selectedCaptionStyle = uiState.selectedCaptionStyle,
                    selectedFilter = uiState.selectedFilter,
                    timedCaptions = uiState.timedCaptions,
                    hookHeadline = uiState.hookHeadline,
                    hashtags = uiState.hashtags,
                    isPro = uiState.isProSubscriber,
                    likeCount = uiState.likeCount,
                    commentCount = uiState.commentCount,
                    isLiked = uiState.isLikedByUser,
                    isSaved = uiState.isSavedByUser,
                    currentBeatStep = uiState.currentBeatStep,
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onToggleMute = { viewModel.toggleMute() },
                    onToggleLike = { viewModel.toggleLike() },
                    onToggleSave = { viewModel.toggleBookmark() },
                    onSeek = { viewModel.seekToPosition(it) },
                    onShare = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${uiState.hookHeadline} #reels #ai"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Reel"))
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("reel_player_view")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- 3. EDITOR BOTTOM TABS SWITCHER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                EditorTabItem(
                    title = "Clips",
                    icon = Icons.Default.ViewCarousel,
                    isSelected = uiState.activeBottomTab == EditorBottomTab.CLIPS,
                    onClick = { viewModel.setActiveTab(EditorBottomTab.CLIPS) }
                )
                EditorTabItem(
                    title = "Beats",
                    icon = Icons.Default.GraphicEq,
                    isSelected = uiState.activeBottomTab == EditorBottomTab.MUSIC,
                    onClick = { viewModel.setActiveTab(EditorBottomTab.MUSIC) }
                )
                EditorTabItem(
                    title = "Captions",
                    icon = Icons.Default.TextFields,
                    isSelected = uiState.activeBottomTab == EditorBottomTab.CAPTIONS,
                    onClick = { viewModel.setActiveTab(EditorBottomTab.CAPTIONS) }
                )
                EditorTabItem(
                    title = "FX & Color",
                    icon = Icons.Default.Tune,
                    isSelected = uiState.activeBottomTab == EditorBottomTab.EFFECTS,
                    onClick = { viewModel.setActiveTab(EditorBottomTab.EFFECTS) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Tab Content
            AnimatedContent(
                targetState = uiState.activeBottomTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_content"
            ) { tab ->
                when (tab) {
                    EditorBottomTab.CLIPS -> {
                        ClipTimelineView(
                            clips = uiState.clips,
                            currentClipIndex = uiState.currentClipIndex,
                            bpm = uiState.selectedTrack.bpm,
                            totalDurationSeconds = uiState.totalDurationSeconds,
                            onClipSelected = { viewModel.seekToPosition(it.toFloat() * 1.5f) },
                            onDurationChanged = { clipId, dur -> viewModel.updateClipDuration(clipId, dur) },
                            onRemoveClip = { viewModel.removeClip(it) },
                            onAddMediaClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                            onAutoBeatMatchClick = { viewModel.autoBeatMatchClips() }
                        )
                    }

                    EditorBottomTab.MUSIC -> {
                        MusicSelectorSheet(
                            selectedTrack = uiState.selectedTrack,
                            isPro = uiState.isProSubscriber,
                            onTrackSelected = { viewModel.setSelectedTrack(it) },
                            onProUpgradeClick = onOpenProModal
                        )
                    }

                    EditorBottomTab.CAPTIONS -> {
                        CaptionStyleSelector(
                            selectedStyle = uiState.selectedCaptionStyle,
                            onStyleSelected = { viewModel.setSelectedCaptionStyle(it) },
                            onOpenAiGenerator = { showAiDialog = true }
                        )
                    }

                    EditorBottomTab.EFFECTS -> {
                        TransitionSelector(
                            selectedTransition = uiState.selectedTransition,
                            selectedFilter = uiState.selectedFilter,
                            onTransitionSelected = { viewModel.setSelectedTransition(it) },
                            onFilterSelected = { viewModel.setSelectedFilter(it) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // AI Script Generator Dialog
    AiHookGeneratorDialog(
        isOpen = showAiDialog,
        isGenerating = uiState.isAiGenerating,
        generationStatus = uiState.aiGenerationStatus,
        onDismiss = { showAiDialog = false },
        onGenerate = { prompt ->
            viewModel.generateAiCaptionsAndHooks(prompt)
        }
    )

    // Export Dialog
    ExportDialog(
        isOpen = showExportDialog,
        exportState = uiState.exportState,
        isPro = uiState.isProSubscriber,
        onDismiss = {
            showExportDialog = false
            viewModel.dismissExport()
        },
        onStartExport = { quality, noWatermark ->
            viewModel.startExport(quality, noWatermark)
        },
        onProUpgradeClick = onOpenProModal
    )
}

@Composable
private fun EditorTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) ReelMagenta else Color.Transparent,
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) TextWhite else TextMuted,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = title,
                color = if (isSelected) TextWhite else TextMuted,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
