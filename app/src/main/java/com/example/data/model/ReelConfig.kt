package com.example.data.model

enum class TransitionType(val displayName: String, val description: String) {
    ZOOM_PULSE("Zoom Pulse", "Dramatic scale beat drop"),
    FLASH_BEAT("White Flash", "High-energy strobe cut"),
    GLITCH("Glitch Shift", "Cyberpunk chromatic aberration"),
    WHIP_PAN("Whip Pan", "Fast cinematic swipe"),
    FADE("Soft Crossfade", "Smooth dreamy blend"),
    CUT("Direct Cut", "Instant snap transition")
}

enum class CaptionStyle(val displayName: String, val previewText: String) {
    KARAOKE_POP("Karaoke Highlight", "Word-by-word synced glowing highlight"),
    NEON_BOX("Neon Cyber Box", "Vibrant glowing backdrop banner"),
    BOLD_HEADLINE("Punchy Impact", "Heavy italic uppercase hook"),
    TYPEWRITER("Typewriter Code", "Character-by-character reveal"),
    MINIMAL_CLEAN("Minimal Sans", "Clean centered modern aesthetic"),
    RETRO_YELLOW("90s Cinema Sub", "Yellow serif subtitle with black drop")
}

enum class ColorFilterType(val displayName: String) {
    NORMAL("Original"),
    VIVID("Vivid Boost"),
    GOLDEN_HOUR("Golden Hour"),
    MOODY_TEAL("Moody Teal"),
    CYBER_NEON("Cyberpunk"),
    NOIR_BW("Black & White"),
    VINTAGE_FILM("Vintage 90s")
}

enum class ExportQuality(val label: String, val resolution: String, val isPro: Boolean) {
    HD_720P("720p HD", "1280x720", false),
    FULL_HD_1080P("1080p Full HD", "1920x1080", false),
    ULTRA_HD_4K("4K Ultra HD", "3840x2160", true)
}

data class TimedCaption(
    val id: String,
    val text: String,
    val startSeconds: Float,
    val endSeconds: Float,
    val hookSubtitle: String = ""
)
