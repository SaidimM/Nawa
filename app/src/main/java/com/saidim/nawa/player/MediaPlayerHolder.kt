package com.saidim.nawa.player

import android.support.v4.media.MediaMetadataCompat

class MediaPlayerHolder {

    var isPlaying = false

    fun getMediaMetadataCompat(): MediaMetadataCompat {
        TODO("pending implement")
    }

    companion object {
        @Volatile private var INSTANCE : MediaPlayerHolder? = null
        fun getInstance(): MediaPlayerHolder {
            if (INSTANCE == null) {
                synchronized(MediaPlayerHolder::class) {
                    if (INSTANCE == null) {
                        INSTANCE = MediaPlayerHolder()
                    }
                }
            }
            return INSTANCE!!
        }

        fun recycle() {
            INSTANCE = null
        }
    }
}