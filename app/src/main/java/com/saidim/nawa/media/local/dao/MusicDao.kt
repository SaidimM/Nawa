package com.saidim.nawa.media.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.saidim.nawa.media.local.bean.Music

@Dao
interface MusicDao {

    @Insert
    suspend fun saveMusic(music: Music): Long

    @Update
    suspend fun updateMusic(music: Music): Int

    @Query("SELECT * FROM music WHERE id = :id LIMIT 1")
    suspend fun getMusic(id: Long): Music?

    @Query("SELECT * FROM music")
    suspend fun getAll(): List<Music>

    @Delete
    suspend fun deleteMusic(music: Music): Int
}