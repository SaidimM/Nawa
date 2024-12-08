package com.saidim.nawa.media.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.saidim.nawa.media.local.bean.PlayHistory
import com.saidim.nawa.media.local.bean.Recent

@Dao
interface RecentDao {
    @Insert
    suspend fun save(recent: Recent): Long

    @Query("SELECT * FROM recent")
    suspend fun getAll(): List<Recent>
}