package com.saidim.nawa.player

import android.support.v4.media.session.MediaSessionCompat

interface IPlayService {
    fun getMediaSession(): MediaSessionCompat
}