package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {
    @Query("SELECT * FROM saved_reels ORDER BY createdAt DESC")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Query("SELECT * FROM saved_reels WHERE id = :id LIMIT 1")
    suspend fun getReelById(id: Int): ReelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelEntity): Long

    @Update
    suspend fun updateReel(reel: ReelEntity)

    @Delete
    suspend fun deleteReel(reel: ReelEntity)

    @Query("DELETE FROM saved_reels WHERE id = :id")
    suspend fun deleteReelById(id: Int)
}
