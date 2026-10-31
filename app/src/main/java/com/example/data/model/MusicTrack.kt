package com.example.data.model

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val bpm: Int,
    val genre: String,
    val durationSeconds: Float = 15.0f,
    val isPro: Boolean = false,
    val audioSynthPattern: AudioSynthPattern = AudioSynthPattern.UPBEAT_ELECTRONIC,
    val beatIntervalSeconds: Float = 60f / bpm.toFloat()
)

enum class AudioSynthPattern {
    UPBEAT_ELECTRONIC,
    LOFI_CHILL,
    CYBER_PHONK,
    SUMMER_POP,
    CINEMATIC_TRAP,
    ACOUSTIC_SUNSET
}

object DefaultMusicTracks {
    val tracks = listOf(
        MusicTrack(
            id = "track_1",
            title = "Midnight Horizon",
            artist = "Neon Dreamer",
            bpm = 120,
            genre = "Cyberwave / Trap",
            isPro = false,
            audioSynthPattern = AudioSynthPattern.UPBEAT_ELECTRONIC
        ),
        MusicTrack(
            id = "track_2",
            title = "Sunset Coffee Vibes",
            artist = "LoFi Botanics",
            bpm = 84,
            genre = "Lo-Fi / Chillhop",
            isPro = false,
            audioSynthPattern = AudioSynthPattern.LOFI_CHILL
        ),
        MusicTrack(
            id = "track_3",
            title = "Tokyo Drift Velocity",
            artist = "Phonk Syndicate",
            bpm = 140,
            genre = "Phonk / Drift",
            isPro = true,
            audioSynthPattern = AudioSynthPattern.CYBER_PHONK
        ),
        MusicTrack(
            id = "track_4",
            title = "Golden Hour Glow",
            artist = "Solar Pop",
            bpm = 112,
            genre = "Indie Pop",
            isPro = false,
            audioSynthPattern = AudioSynthPattern.SUMMER_POP
        ),
        MusicTrack(
            id = "track_5",
            title = "Epic Ascent 808",
            artist = "Apex Beats",
            bpm = 132,
            genre = "Cinematic Hype",
            isPro = true,
            audioSynthPattern = AudioSynthPattern.CINEMATIC_TRAP
        ),
        MusicTrack(
            id = "track_6",
            title = "Warm Breeze Acoustic",
            artist = "Coastal Strings",
            bpm = 96,
            genre = "Acoustic / Travel",
            isPro = false,
            audioSynthPattern = AudioSynthPattern.ACOUSTIC_SUNSET
        )
    )
}
