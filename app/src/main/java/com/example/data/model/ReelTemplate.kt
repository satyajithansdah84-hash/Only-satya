package com.example.data.model

import com.example.R

data class ReelTemplate(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val previewImageRes: Int,
    val musicTrack: MusicTrack,
    val transition: TransitionType,
    val captionStyle: CaptionStyle,
    val filterType: ColorFilterType,
    val clipDurations: List<Float>,
    val defaultCaptions: List<TimedCaption>,
    val isPro: Boolean = false,
    val tagLabel: String = "Trending"
)

object DefaultTemplates {
    val templates = listOf(
        ReelTemplate(
            id = "tmpl_travel",
            title = "Cinematic Travel Odyssey",
            category = "Travel & Adventure",
            description = "Sweeping golden hour pans with beat-synced cuts and karaoke subtitles.",
            previewImageRes = R.drawable.sample_travel,
            musicTrack = DefaultMusicTracks.tracks[0], // Midnight Horizon
            transition = TransitionType.ZOOM_PULSE,
            captionStyle = CaptionStyle.KARAOKE_POP,
            filterType = ColorFilterType.GOLDEN_HOUR,
            clipDurations = listOf(1.8f, 1.4f, 1.8f, 2.0f),
            defaultCaptions = listOf(
                TimedCaption("c1", "POV: You booked the one-way flight", 0f, 2.0f, "Best decision ever ✨"),
                TimedCaption("c2", "Lost somewhere where Wi-Fi is weak", 2.0f, 4.5f, "Pure bliss 🌊"),
                TimedCaption("c3", "The world is too big to stay in one place", 4.5f, 7.0f, "Save for your bucket list 📍")
            ),
            isPro = false,
            tagLabel = "Viral"
        ),
        ReelTemplate(
            id = "tmpl_cyber",
            title = "Cyber Tokyo Drift",
            category = "Hype & City",
            description = "High-energy phonk tempo with neon chromatic glitch shifts and neon box text.",
            previewImageRes = R.drawable.sample_cyber,
            musicTrack = DefaultMusicTracks.tracks[2], // Tokyo Drift Velocity
            transition = TransitionType.GLITCH,
            captionStyle = CaptionStyle.NEON_BOX,
            filterType = ColorFilterType.CYBER_NEON,
            clipDurations = listOf(1.0f, 0.8f, 1.0f, 1.2f),
            defaultCaptions = listOf(
                TimedCaption("c4", "LIGHTS DON'T SLEEP HERE ⚡", 0f, 1.8f, "City that never dies"),
                TimedCaption("c5", "SPEED RUNNING 2026 🏎️", 1.8f, 3.5f, "Can't slow down"),
                TimedCaption("c6", "FULL THROTTLE ENERGY 🔥", 3.5f, 5.0f, "Tag your squad")
            ),
            isPro = true,
            tagLabel = "PRO VIP"
        ),
        ReelTemplate(
            id = "tmpl_fitness",
            title = "Beast Mode Workout",
            category = "Fitness & Motivation",
            description = "Punchy 808 beat drops synced to peak athletic movements with white strobe flash.",
            previewImageRes = R.drawable.sample_fitness,
            musicTrack = DefaultMusicTracks.tracks[4], // Epic Ascent 808
            transition = TransitionType.FLASH_BEAT,
            captionStyle = CaptionStyle.BOLD_HEADLINE,
            filterType = ColorFilterType.VIVID,
            clipDurations = listOf(1.2f, 1.0f, 1.4f, 1.5f),
            defaultCaptions = listOf(
                TimedCaption("c7", "NO EXCUSES. ONLY RESULTS.", 0f, 1.5f, "Day 1 vs One Day"),
                TimedCaption("c8", "OUTWORK YOUR DOUBTS TODAY.", 1.5f, 3.2f, "Discipline over motivation"),
                TimedCaption("c9", "THE ONLY REEL YOU NEED TO SEE.", 3.2f, 5.1f, "Save & crush today's session 💥")
            ),
            isPro = true,
            tagLabel = "PRO"
        ),
        ReelTemplate(
            id = "tmpl_aesthetic",
            title = "Sunday Morning Coffee",
            category = "Lifestyle & Vlog",
            description = "Cozy slow-tempo chillhop with soft crossfade transitions and minimalist text.",
            previewImageRes = R.drawable.sample_travel,
            musicTrack = DefaultMusicTracks.tracks[1], // Sunset Coffee Vibes
            transition = TransitionType.FADE,
            captionStyle = CaptionStyle.MINIMAL_CLEAN,
            filterType = ColorFilterType.MOODY_TEAL,
            clipDurations = listOf(2.2f, 2.0f, 2.2f, 2.5f),
            defaultCaptions = listOf(
                TimedCaption("c10", "Sundays are meant for slowing down.", 0f, 2.5f, "Warm coffee & quiet thoughts"),
                TimedCaption("c11", "Finding beauty in the simple moments.", 2.5f, 5.0f, "Take a deep breath"),
                TimedCaption("c12", "Ready for a fresh new week ahead.", 5.0f, 8.0f, "Comment your Sunday vibe ☕")
            ),
            isPro = false,
            tagLabel = "Popular"
        )
    )
}
