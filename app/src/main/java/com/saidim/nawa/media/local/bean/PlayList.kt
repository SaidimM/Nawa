package com.saidim.nawa.media.local.bean

import LogUtil
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity
data class PlayList(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var name: String = "",
    var description: String = "",
    var cover: String = "",
    var createTime: Long = System.currentTimeMillis(),
    var updateTime: Long = System.currentTimeMillis(),
    var songList: String = ""
) {
    fun getList(): List<Music> {
        try {
            return Gson().fromJson(songList, object: TypeToken<List<Music>>() {})
        } catch (exception: Exception) {
            LogUtil.e(this.javaClass.simpleName, exception.message.toString())
            return emptyList()
        }
    }

    fun setList(lists: List<Music>) {
        songList = try {
            Gson().toJson(lists)
        } catch (exception: Exception) {
            LogUtil.e(this.javaClass.simpleName, exception.message.toString())
            ""
        }
    }
}