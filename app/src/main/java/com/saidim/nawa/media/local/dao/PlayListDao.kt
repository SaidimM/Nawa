package com.saidim.nawa.media.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.saidim.nawa.media.local.bean.PlayList

@Dao
interface PlayListDao {

    @Insert
    suspend fun save(playList: PlayList): Long

    @Delete
    suspend fun delete(playList: PlayList): Int

    @Update
    suspend fun update(playList: PlayList): Int

    @Query("SELECT * FROM playlist")
    suspend fun getAll(): List<PlayList>

    @Query("SELECT * FROM playlist WHERE name = 'favorite'")
    suspend fun getFavorites(): PlayList
}