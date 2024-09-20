package com.saidim.nawa.player

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.support.v4.media.session.MediaSessionCompat

class PlayService: IPlayService, Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService() = this@PlayService
    }

    override fun onBind(intent: Intent) = binder
    override fun getMediaSession(): MediaSessionCompat {
        TODO("Not yet implemented")
    }
}