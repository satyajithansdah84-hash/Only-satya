package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.audio.BeatAudioSynthesizer
import com.example.data.local.AppDatabase
import com.example.data.local.ReelEntity
import com.example.data.model.CaptionStyle
import com.example.data.model.ColorFilterType
import com.example.data.model.DefaultMusicTracks
import com.example.data.model.DefaultTemplates
import com.example.data.model.ExportQuality
import com.example.data.model.MediaClip
import com.example.data.model.MediaType
import com.example.data.model.MusicTrack
import com.example.data.model.ReelTemplate
import com.example.data.model.TimedCaption
import com.example.data.model.TransitionType
import com.example.data.remote.GeminiReelService
import com.example.data.repository.ReelRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class EditorBottomTab {
    CLIPS, MUSIC, CAPTIONS, EFFECTS
}

sealed class ExportState {
    object Idle : ExportState()
    data class Rendering(val progress: Float, val stage: String) : ExportState()
    data class Success(val title: String, val resolution: String, val hasWatermark: Boolean) : ExportState()
}

data class ReelUiState(
    val clips: List<MediaClip> = emptyList(),
    val currentClipIndex: Int = 0,
    val playbackPositionSeconds: Float = 0f,
    val totalDurationSeconds: Float = 6.0f,
    val isPlaying: Boolean = false,
    val isMuted: Boolean = false,
    val selectedTrack: MusicTrack = DefaultMusicTracks.tracks[0],
    val selectedTransition: TransitionType = TransitionType.ZOOM_PULSE,
    val selectedCaptionStyle: CaptionStyle = CaptionStyle.KARAOKE_POP,
    val selectedFilter: ColorFilterType = ColorFilterType.NORMAL,
    val timedCaptions: List<TimedCaption> = emptyList(),
    val reelTitle: String = "My AI Reel",
    val hookHeadline: String = "Wait for the beat drop... ✨",
    val hashtags: List<String> = listOf("#reels", "#fyp", "#viral", "#aesthetic"),
    val isProSubscriber: Boolean = false,
    val isAiGenerating: Boolean = false,
    val aiGenerationStatus: String = "",
    val activeBottomTab: EditorBottomTab = EditorBottomTab.CLIPS,
    val exportState: ExportState = ExportState.Idle,
    val likeCount: Int = 1420,
    val commentCount: Int = 89,
    val isLikedByUser: Boolean = false,
    val isSavedByUser: Boolean = false,
    val currentBeatStep: Int = 0
)

class ReelViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReelRepository
    private val geminiService = GeminiReelService()
    private val audioSynthesizer = BeatAudioSynthesizer()

    private val _uiState = MutableStateFlow(ReelUiState())
    val uiState: StateFlow<ReelUiState> = _uiState.asStateFlow()

    private var playbackLoopJob: Job? = null

    val savedReels: StateFlow<List<ReelEntity>>

    init {
        val database = AppDatabase.getInstance(application)
        repository = ReelRepository(database.reelDao())
        savedReels = repository.allSavedReels.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Initialize with high quality sample clips so user immediately sees a working reel
        loadSampleStockClips()
    }

    fun loadSampleStockClips() {
        val stockClips = listOf(
            MediaClip(
                id = "clip_stock_1",
                drawableResId = R.drawable.sample_travel,
                mediaType = MediaType.IMAGE,
                durationSeconds = 1.8f,
                promptDescription = "Tropical cliff ocean waves at sunset"
            ),
            MediaClip(
                id = "clip_stock_2",
                drawableResId = R.drawable.sample_cyber,
                mediaType = MediaType.IMAGE,
                durationSeconds = 1.5f,
                promptDescription = "Neon Tokyo night reflections"
            ),
            MediaClip(
                id = "clip_stock_3",
                drawableResId = R.drawable.sample_fitness,
                mediaType = MediaType.IMAGE,
                durationSeconds = 2.0f,
                promptDescription = "Athletic sunrise sprint"
            )
        )

        val totalDuration = stockClips.sumOf { it.durationSeconds.toDouble() }.toFloat()
        val defaultCaptions = listOf(
            TimedCaption("cap_1", "POV: You finally chased the dream", 0f, 1.8f, "Best feeling in the world ✨"),
            TimedCaption("cap_2", "NO SLEEP. ONLY FOCUS. ⚡", 1.8f, 3.3f, "Day 1 vs One day"),
            TimedCaption("cap_3", "The world is yours to explore 🌍", 3.3f, totalDuration, "Save for your bucket list 📍")
        )

        _uiState.update {
            it.copy(
                clips = stockClips,
                totalDurationSeconds = totalDuration,
                timedCaptions = defaultCaptions,
                reelTitle = "Chasing Horizons",
                hookHeadline = "POV: You finally booked the ticket ✈️",
                hashtags = listOf("#travelreels", "#fyp", "#viral", "#aesthetic", "#dreambig")
            )
        }
    }

    fun addClipsFromUris(uris: List<String>) {
        if (uris.isEmpty()) return
        val newClips = uris.mapIndexed { index, uri ->
            MediaClip(
                id = "user_clip_${System.currentTimeMillis()}_$index",
                uriString = uri,
                mediaType = MediaType.IMAGE,
                durationSeconds = 1.5f,
                promptDescription = "User uploaded media"
            )
        }
        val updated = _uiState.value.clips + newClips
        val totalDur = updated.sumOf { it.durationSeconds.toDouble() }.toFloat()
        _uiState.update {
            it.copy(
                clips = updated,
                totalDurationSeconds = totalDur
            )
        }
        autoBeatMatchClips()
    }

    fun applyTemplate(template: ReelTemplate) {
        val clips = if (_uiState.value.clips.isNotEmpty()) {
            _uiState.value.clips.mapIndexed { index, clip ->
                val newDur = template.clipDurations.getOrNull(index) ?: 1.5f
                clip.copy(durationSeconds = newDur)
            }
        } else {
            listOf(
                MediaClip("tmpl_clip_1", drawableResId = template.previewImageRes, durationSeconds = 1.8f)
            )
        }

        val totalDur = clips.sumOf { it.durationSeconds.toDouble() }.toFloat()

        _uiState.update {
            it.copy(
                clips = clips,
                totalDurationSeconds = totalDur,
                selectedTrack = template.musicTrack,
                selectedTransition = template.transition,
                selectedCaptionStyle = template.captionStyle,
                selectedFilter = template.filterType,
                timedCaptions = template.defaultCaptions,
                reelTitle = template.title
            )
        }

        if (_uiState.value.isPlaying) {
            startAudioPlayback()
        }
    }

    fun autoBeatMatchClips() {
        val state = _uiState.value
        if (state.clips.isEmpty()) return

        val bpm = state.selectedTrack.bpm
        // Measure beat subdivision interval (e.g. 2 beats or 4 beats per cut)
        val singleBeatSeconds = 60f / bpm.toFloat()
        val beatsPerClip = when {
            bpm >= 130 -> 2 // 2 beats for high tempo
            bpm >= 100 -> 3 // 3 beats
            else -> 4 // 4 beats for chill tempo
        }
        val targetClipDuration = (singleBeatSeconds * beatsPerClip).coerceIn(0.8f, 3.5f)

        val updatedClips = state.clips.map { clip ->
            clip.copy(durationSeconds = targetClipDuration)
        }
        val newTotal = updatedClips.sumOf { it.durationSeconds.toDouble() }.toFloat()

        // Re-align captions to fit new clip boundaries
        val updatedCaptions = state.timedCaptions.mapIndexed { idx, cap ->
            val start = (idx * targetClipDuration).coerceAtMost(newTotal)
            val end = ((idx + 1) * targetClipDuration).coerceAtMost(newTotal)
            cap.copy(startSeconds = start, endSeconds = end)
        }

        _uiState.update {
            it.copy(
                clips = updatedClips,
                totalDurationSeconds = newTotal,
                timedCaptions = updatedCaptions
            )
        }
    }

    fun updateClipDuration(clipId: String, newDuration: Float) {
        val clamped = newDuration.coerceIn(0.5f, 5.0f)
        val updated = _uiState.value.clips.map {
            if (it.id == clipId) it.copy(durationSeconds = clamped) else it
        }
        val totalDur = updated.sumOf { it.durationSeconds.toDouble() }.toFloat()
        _uiState.update {
            it.copy(clips = updated, totalDurationSeconds = totalDur)
        }
    }

    fun removeClip(clipId: String) {
        val updated = _uiState.value.clips.filterNot { it.id == clipId }
        val totalDur = updated.sumOf { it.durationSeconds.toDouble() }.toFloat().coerceAtLeast(1.0f)
        _uiState.update {
            it.copy(
                clips = updated,
                totalDurationSeconds = totalDur,
                currentClipIndex = it.currentClipIndex.coerceAtMost((updated.size - 1).coerceAtLeast(0))
            )
        }
    }

    fun togglePlayPause() {
        val currentlyPlaying = _uiState.value.isPlaying
        if (currentlyPlaying) {
            stopPlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        _uiState.update { it.copy(isPlaying = true) }
        startAudioPlayback()

        playbackLoopJob?.cancel()
        playbackLoopJob = viewModelScope.launch {
            val frameIntervalMs = 33L // ~30 fps
            while (isActive && _uiState.value.isPlaying) {
                delay(frameIntervalMs)
                val state = _uiState.value
                val newPos = state.playbackPositionSeconds + (frameIntervalMs / 1000f)

                if (newPos >= state.totalDurationSeconds) {
                    // Loop reel seamlessly like Instagram Reels
                    _uiState.update {
                        it.copy(
                            playbackPositionSeconds = 0f,
                            currentClipIndex = 0
                        )
                    }
                } else {
                    // Calculate which clip we are currently on
                    var accumulated = 0f
                    var foundClipIndex = 0
                    for (i in state.clips.indices) {
                        val clipEnd = accumulated + state.clips[i].durationSeconds
                        if (newPos in accumulated..clipEnd) {
                            foundClipIndex = i
                            break
                        }
                        accumulated = clipEnd
                    }

                    _uiState.update {
                        it.copy(
                            playbackPositionSeconds = newPos,
                            currentClipIndex = foundClipIndex
                        )
                    }
                }
            }
        }
    }

    private fun startAudioPlayback() {
        audioSynthesizer.setMuted(_uiState.value.isMuted)
        audioSynthesizer.startBeatTrack(_uiState.value.selectedTrack) { beatIndex ->
            _uiState.update { it.copy(currentBeatStep = beatIndex) }
        }
    }

    private fun stopPlayback() {
        playbackLoopJob?.cancel()
        playbackLoopJob = null
        audioSynthesizer.stopBeatTrack()
        _uiState.update { it.copy(isPlaying = false) }
    }

    fun seekToPosition(positionSec: Float) {
        val total = _uiState.value.totalDurationSeconds
        val clamped = positionSec.coerceIn(0f, total)

        var accumulated = 0f
        var foundClipIndex = 0
        for (i in _uiState.value.clips.indices) {
            val clipEnd = accumulated + _uiState.value.clips[i].durationSeconds
            if (clamped in accumulated..clipEnd) {
                foundClipIndex = i
                break
            }
            accumulated = clipEnd
        }

        _uiState.update {
            it.copy(
                playbackPositionSeconds = clamped,
                currentClipIndex = foundClipIndex
            )
        }
    }

    fun toggleMute() {
        val newMute = !_uiState.value.isMuted
        _uiState.update { it.copy(isMuted = newMute) }
        audioSynthesizer.setMuted(newMute)
    }

    fun toggleLike() {
        _uiState.update {
            val nextLiked = !it.isLikedByUser
            it.copy(
                isLikedByUser = nextLiked,
                likeCount = if (nextLiked) it.likeCount + 1 else it.likeCount - 1
            )
        }
    }

    fun toggleBookmark() {
        _uiState.update { it.copy(isSavedByUser = !it.isSavedByUser) }
    }

    fun setActiveTab(tab: EditorBottomTab) {
        _uiState.update { it.copy(activeBottomTab = tab) }
    }

    fun setSelectedTrack(track: MusicTrack) {
        _uiState.update { it.copy(selectedTrack = track) }
        if (_uiState.value.isPlaying) {
            startAudioPlayback()
        }
    }

    fun setSelectedTransition(trans: TransitionType) {
        _uiState.update { it.copy(selectedTransition = trans) }
    }

    fun setSelectedCaptionStyle(style: CaptionStyle) {
        _uiState.update { it.copy(selectedCaptionStyle = style) }
    }

    fun setSelectedFilter(filter: ColorFilterType) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun setReelTitle(title: String) {
        _uiState.update { it.copy(reelTitle = title) }
    }

    fun generateAiCaptionsAndHooks(topicOrVibe: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAiGenerating = true,
                    aiGenerationStatus = "Analyzing scene aesthetic & beat drops..."
                )
            }

            delay(600)
            _uiState.update {
                it.copy(aiGenerationStatus = "Crafting high-retention viral hook with Gemini...")
            }

            val result = geminiService.generateReelContent(
                topicOrVibe = topicOrVibe,
                clipCount = _uiState.value.clips.size,
                totalDurationSeconds = _uiState.value.totalDurationSeconds
            )

            // Match suggested transition
            val matchingTransition = TransitionType.values().firstOrNull {
                it.name.equals(result.suggestedTransition, ignoreCase = true)
            } ?: _uiState.value.selectedTransition

            _uiState.update {
                it.copy(
                    isAiGenerating = false,
                    aiGenerationStatus = "",
                    reelTitle = result.title,
                    hookHeadline = result.hookHeadline,
                    timedCaptions = result.captions,
                    hashtags = result.hashtags,
                    selectedTransition = matchingTransition
                )
            }
        }
    }

    fun upgradeToPro() {
        _uiState.update { it.copy(isProSubscriber = true) }
    }

    fun startExport(quality: ExportQuality, removeWatermark: Boolean) {
        viewModelScope.launch {
            stopPlayback()
            val stages = listOf(
                "Stitching high-res clips into 9:16 vertical stream...",
                "Synthesizing beat audio & alignment (${_uiState.value.selectedTrack.bpm} BPM)...",
                "Applying ${_uiState.value.selectedTransition.displayName} & ${_uiState.value.selectedFilter.displayName} filter...",
                "Rendering dynamic ${_uiState.value.selectedCaptionStyle.displayName} overlay...",
                if (removeWatermark) "Pro Clean Export: Zero Watermark" else "Finalizing Reel..."
            )

            for (i in stages.indices) {
                val progress = (i + 1).toFloat() / stages.size
                _uiState.update {
                    it.copy(
                        exportState = ExportState.Rendering(progress = progress, stage = stages[i])
                    )
                }
                delay(600)
            }

            // Save completed reel to Room Database!
            val entity = ReelEntity(
                title = _uiState.value.reelTitle,
                mediaClipsJson = "${_uiState.value.clips.size} clips",
                captionText = _uiState.value.timedCaptions.joinToString(" | ") { it.text },
                hookText = _uiState.value.hookHeadline,
                musicTrackId = _uiState.value.selectedTrack.id,
                transitionType = _uiState.value.selectedTransition.name,
                captionStyle = _uiState.value.selectedCaptionStyle.name,
                filterType = _uiState.value.selectedFilter.name,
                isPro = _uiState.value.isProSubscriber,
                durationSeconds = _uiState.value.totalDurationSeconds,
                exportQuality = quality.name,
                thumbnailResId = _uiState.value.clips.firstOrNull()?.drawableResId ?: R.drawable.sample_travel
            )
            repository.saveReel(entity)

            _uiState.update {
                it.copy(
                    exportState = ExportState.Success(
                        title = it.reelTitle,
                        resolution = quality.resolution,
                        hasWatermark = !removeWatermark
                    )
                )
            }
        }
    }

    fun dismissExport() {
        _uiState.update { it.copy(exportState = ExportState.Idle) }
    }

    fun deleteSavedReel(id: Int) {
        viewModelScope.launch {
            repository.deleteReel(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
    }
}
