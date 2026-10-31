package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_reels")
data class ReelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val mediaClipsJson: String,
    val captionText: String,
    val hookText: String,
    val musicTrackId: String,
    val transitionType: String,
    val captionStyle: String,
    val filterType: String,
    val isPro: Boolean = false,
    val isFavorite: Boolean = false,
    val durationSeconds: Float = 6.0f,
    val exportQuality: String = "FULL_HD_1080P",
    val thumbnailResId: Int = 0
)
