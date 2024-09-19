package com.saidim.nawa.player

import android.app.Service
import android.content.Intent
import android.os.Binder

class PlayService: Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService() = this@PlayService
    }

    override fun onBind(intent: Intent) = binder
}