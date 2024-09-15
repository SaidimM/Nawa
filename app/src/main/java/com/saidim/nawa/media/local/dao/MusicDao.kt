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
    fun saveMusic(music: Music): Long

    @Update
    fun updateMusic(music: Music): Int

    @Query("SELECT * FROM music WHERE id = :id LIMIT 1")
    fun getMusic(id: Long): Music?

    @Query("SELECT * FROM music")
    fun getAll(): List<Music>

    @Delete
    fun deleteMusic(music: Music): Int
}