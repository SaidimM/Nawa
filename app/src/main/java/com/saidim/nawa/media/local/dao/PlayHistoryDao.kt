package com.saidim.nawa.media.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.saidim.nawa.media.local.bean.PlayHistory

@Dao
interface PlayHistoryDao {
    @Insert
    fun save(playHistory: PlayHistory): Long

    @Query("SELECT * FROM playhistory")
    fun getAll(): List<PlayHistory>
}