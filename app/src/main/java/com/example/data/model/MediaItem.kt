package com.example.data.model

import android.net.Uri

enum class MediaType {
    IMAGE, VIDEO
}

data class MediaClip(
    val id: String,
    val uriString: String? = null,
    val drawableResId: Int? = null,
    val mediaType: MediaType = MediaType.IMAGE,
    val durationSeconds: Float = 2.0f,
    val promptDescription: String = "",
    val speedMultiplier: Float = 1.0f
)
