package com.saidim.nawa

import com.blankj.utilcode.util.Utils
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.base.utils.EaseCubicInterpolator

object Constants {
    val ALBUM_COVER_DIR = Utils.getApp().getExternalFilesDir("")!!.absolutePath + "/album/"
    val LYRIC_DIR = Utils.getApp().getExternalFilesDir("")!!.absolutePath + "/lyric/"

    val bezierInterpolator = EaseCubicInterpolator(0.25f, 0.25f, 0.15f, 1f)

    const val MUSIC_ID = "MUSIC_ID"

    const val NOTIFICATION_CHANNEL_ID: String = "NOTIFICATION_CHANNEL_ID"
    const val CLOSE_ACTION: String = "CLOSE_ACTION"
    const val PRE_ACTION: String = "PRE_ACTION"
    const val NEXT_ACTION: String = "NEXT_ACTION"
    const val PLAY_PAUSE_ACTION: String = "PLAY_PAUSE_ACTION"
    const val NOTIFICATION_INTENT_REQUEST_CODE = 100

    const val NOTIFICATION_CHANNEL_ERROR_ID = "CHANNEL_ERROR"
    const val NOTIFICATION_ID = 101
    const val NOTIFICATION_ERROR_ID = 102
    const val FAST_FORWARD_ACTION = "FAST_FORWARD_GO"
    const val PREV_ACTION = "PREV_ACTION"
    const val REWIND_ACTION = "REWIND_ACTION"
    const val REPEAT_ACTION = "REPEAT_ACTION"
    const val FAVORITE_ACTION = "FAVORITE_ACTION"
    const val FAVORITE_POSITION_ACTION = "FAVORITE_POSITION_GO"

    fun getAlbumCoverPath(music: Music) = "$ALBUM_COVER_DIR${music.id}.jpg"

    fun getLyricPath(music: Music) = "$LYRIC_DIR${music.id}.txt"
}