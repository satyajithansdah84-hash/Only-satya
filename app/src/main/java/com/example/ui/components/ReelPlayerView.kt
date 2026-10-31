package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.CaptionStyle
import com.example.data.model.ColorFilterType
import com.example.data.model.MediaClip
import com.example.data.model.MusicTrack
import com.example.data.model.TimedCaption
import com.example.data.model.TransitionType
import com.example.ui.theme.*

@Composable
fun ReelPlayerView(
    clips: List<MediaClip>,
    currentClipIndex: Int,
    playbackPositionSeconds: Float,
    totalDurationSeconds: Float,
    isPlaying: Boolean,
    isMuted: Boolean,
    selectedTrack: MusicTrack,
    selectedTransition: TransitionType,
    selectedCaptionStyle: CaptionStyle,
    selectedFilter: ColorFilterType,
    timedCaptions: List<TimedCaption>,
    hookHeadline: String,
    hashtags: List<String>,
    isPro: Boolean,
    likeCount: Int,
    commentCount: Int,
    isLiked: Boolean,
    isSaved: Boolean,
    currentBeatStep: Int,
    onTogglePlay: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit,
    onSeek: (Float) -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentClip = clips.getOrNull(currentClipIndex)
    val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_spin"
    )

    // Beat pulse animation when music beats hit
    val beatScale = remember { Animatable(1f) }
    LaunchedEffect(currentBeatStep) {
        if (currentBeatStep % 2 == 0) {
            beatScale.animateTo(1.05f, animationSpec = tween(60, easing = FastOutSlowInEasing))
            beatScale.animateTo(1.0f, animationSpec = tween(120, easing = FastOutSlowInEasing))
        }
    }

    // Transition effect calculation based on clip progress
    var clipProgress = 0f
    var accumulated = 0f
    for (i in 0 until currentClipIndex) {
        accumulated += clips.getOrNull(i)?.durationSeconds ?: 0f
    }
    val currentClipDur = currentClip?.durationSeconds ?: 1.5f
    val timeInCurrentClip = (playbackPositionSeconds - accumulated).coerceIn(0f, currentClipDur)
    clipProgress = timeInCurrentClip / currentClipDur

    // Flash / Zoom transition trigger at cut onset
    val isNearTransition = timeInCurrentClip < 0.22f && currentClipIndex > 0

    // Find active caption
    val activeCaption = timedCaptions.firstOrNull {
        playbackPositionSeconds >= it.startSeconds && playbackPositionSeconds <= it.endSeconds
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkBg)
            .border(1.dp, Color(0xFF262632), RoundedCornerShape(20.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onTogglePlay() },
        contentAlignment = Alignment.Center
    ) {
        // --- 1. MEDIA CLIP DISPLAY (9:16 Aspect) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(if (isNearTransition && selectedTransition == TransitionType.ZOOM_PULSE) 1.12f else beatScale.value)
        ) {
            if (currentClip != null) {
                val colorFilter = when (selectedFilter) {
                    ColorFilterType.NORMAL -> null
                    ColorFilterType.VIVID -> ColorFilter.colorMatrix(ColorMatrix().apply {
                        setToSaturation(1.4f)
                    })
                    ColorFilterType.GOLDEN_HOUR -> ColorFilter.tint(Color(0xFFFFB300).copy(alpha = 0.2f), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                    ColorFilterType.MOODY_TEAL -> ColorFilter.tint(Color(0xFF00897B).copy(alpha = 0.18f), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                    ColorFilterType.CYBER_NEON -> ColorFilter.tint(NeonCyan.copy(alpha = 0.15f), androidx.compose.ui.graphics.BlendMode.Overlay)
                    ColorFilterType.NOIR_BW -> ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                    ColorFilterType.VINTAGE_FILM -> ColorFilter.colorMatrix(ColorMatrix().apply {
                        setToSaturation(0.85f)
                    })
                }

                if (currentClip.uriString != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentClip.uriString)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Reel Clip",
                        contentScale = ContentScale.Crop,
                        colorFilter = colorFilter,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (currentClip.drawableResId != null) {
                    Image(
                        painter = painterResource(id = currentClip.drawableResId),
                        contentDescription = "Reel Stock Clip",
                        contentScale = ContentScale.Crop,
                        colorFilter = colorFilter,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF14141A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No clips added yet", color = TextMuted)
                }
            }

            // Strobe White Flash transition
            if (isNearTransition && selectedTransition == TransitionType.FLASH_BEAT) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.65f))
                )
            }

            // Glitch chromatic aberration bar
            if (isNearTransition && selectedTransition == TransitionType.GLITCH) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = 6.dp)
                        .background(NeonPink.copy(alpha = 0.35f))
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = (-6).dp)
                        .background(NeonCyan.copy(alpha = 0.35f))
                )
            }

            // Vignette & gradient overlays for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
        }

        // --- 2. TOP SEGMENTED PROGRESS BARS (Like Instagram Stories / Reels) ---
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                var previousClipsTime = 0f
                clips.forEachIndexed { index, clip ->
                    val clipDuration = clip.durationSeconds
                    val clipStart = previousClipsTime
                    val clipEnd = previousClipsTime + clipDuration
                    previousClipsTime = clipEnd

                    val fillRatio = when {
                        playbackPositionSeconds >= clipEnd -> 1.0f
                        playbackPositionSeconds <= clipStart -> 0.0f
                        else -> (playbackPositionSeconds - clipStart) / clipDuration
                    }.coerceIn(0f, 1f)

                    Box(
                        modifier = Modifier
                            .weight(clipDuration)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fillRatio)
                                .height(3.dp)
                                .background(Color.White)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sound status & Live BPM indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "BPM",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${selectedTrack.bpm} BPM Beat Sync",
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Mute",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // --- 3. DYNAMIC ANIMATED CAPTIONS OVERLAY ---
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            val captionToRender = activeCaption?.text ?: hookHeadline
            val subCaption = activeCaption?.hookSubtitle ?: ""

            when (selectedCaptionStyle) {
                CaptionStyle.KARAOKE_POP -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            color = ReelMagenta.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 8.dp
                        ) {
                            Text(
                                text = captionToRender.uppercase(),
                                color = TextWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                        if (subCaption.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subCaption,
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                CaptionStyle.NEON_BOX -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(ReelPurple.copy(alpha = 0.85f), ReelMagenta.copy(alpha = 0.85f))
                                )
                            )
                            .border(1.5.dp, NeonCyan, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = captionToRender,
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            if (subCaption.isNotBlank()) {
                                Text(
                                    text = subCaption,
                                    color = NeonCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                CaptionStyle.BOLD_HEADLINE -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = captionToRender.uppercase(),
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        if (subCaption.isNotBlank()) {
                            Text(
                                text = subCaption,
                                color = ReelOrange,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                CaptionStyle.TYPEWRITER -> {
                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF444455))
                    ) {
                        Text(
                            text = "$captionToRender _",
                            fontFamily = FontFamily.Monospace,
                            color = NeonGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                CaptionStyle.RETRO_YELLOW -> {
                    Text(
                        text = captionToRender,
                        color = Color(0xFFFFEB3B),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.shadow(4.dp, shape = CircleShape)
                    )
                }
                CaptionStyle.MINIMAL_CLEAN -> {
                    Text(
                        text = captionToRender,
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Play/Pause central indicator
        AnimatedVisibility(
            visible = !isPlaying,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // --- 4. INSTAGRAM SIDE ACTIONS RAIL (Right Side) ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onToggleLike,
                    modifier = Modifier.testTag("reel_like_button")
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) ReelRed else TextWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "$likeCount",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Comment Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = TextWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = "$commentCount",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Share Button
            IconButton(onClick = onShare, modifier = Modifier.testTag("reel_share_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Share",
                    tint = TextWhite,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Bookmark / Save
            IconButton(onClick = onToggleSave) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save",
                    tint = TextWhite,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Rotating Audio Vinyl Disc
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .rotate(if (isPlaying) discRotation else 0f)
                    .clip(CircleShape)
                    .background(Color(0xFF222228))
                    .border(2.dp, Color(0xFF444450), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(ReelMagenta)
                )
            }
        }

        // --- 5. BOTTOM REEL INFO OVERLAY (Creator, Caption, Music Track) ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.8f)
                .padding(start = 14.dp, bottom = 24.dp)
        ) {
            // Watermark (Free vs Pro)
            if (!isPro) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Made with ReelAI",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // User handle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
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
                        Text("AI", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "@reelmaker.ai",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Caption & Hashtags
            Text(
                text = "$hookHeadline ${hashtags.take(3).joinToString(" ")}",
                color = TextWhite,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Music Ticker Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Music",
                    tint = TextWhite,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${selectedTrack.title} • ${selectedTrack.artist}",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
