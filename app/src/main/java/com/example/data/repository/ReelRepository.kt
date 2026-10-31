package com.example.data.repository

import com.example.data.local.ReelDao
import com.example.data.local.ReelEntity
import kotlinx.coroutines.flow.Flow

class ReelRepository(private val reelDao: ReelDao) {
    val allSavedReels: Flow<List<ReelEntity>> = reelDao.getAllReels()

    suspend fun saveReel(reel: ReelEntity): Long {
        return reelDao.insertReel(reel)
    }

    suspend fun getReel(id: Int): ReelEntity? {
        return reelDao.getReelById(id)
    }

    suspend fun updateReel(reel: ReelEntity) {
        reelDao.updateReel(reel)
    }

    suspend fun deleteReel(id: Int) {
        reelDao.deleteReelById(id)
    }
}
