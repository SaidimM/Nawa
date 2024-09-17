package com.saidim.nawa.media.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blankj.utilcode.util.Utils
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.local.bean.PlayHistory
import com.saidim.nawa.media.local.bean.PlayList
import com.saidim.nawa.media.local.dao.MusicDao
import com.saidim.nawa.media.local.dao.PlayHistoryDao
import com.saidim.nawa.media.local.dao.PlayListDao

//@Database(entities = [Music::class, PlayList::class, PlayHistory::class], version = 1)
//abstract class NawaDatabase : RoomDatabase() {
//
//    abstract fun getMusicDao(): MusicDao
//    abstract fun getPlayListDao(): PlayListDao
//    abstract fun getPlayHistoryDao(): PlayHistoryDao
//
//    companion object {
//        private const val DATABASE_NAME = "nawa.db"
//        private var instance: NawaDatabase? = null
//        fun getInstance(): NawaDatabase {
//            if (instance == null) {
//                instance = Room.databaseBuilder(Utils.getApp(), NawaDatabase::class.java, DATABASE_NAME).build()
//            }
//            return instance!!
//        }
//    }
//}