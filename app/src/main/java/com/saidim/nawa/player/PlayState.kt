package com.saidim.nawa.player

import android.support.v4.media.session.PlaybackStateCompat

enum class PlayState(val value: Int) {
    PLAYING(PlaybackStateCompat.STATE_PLAYING),
    PAUSED(PlaybackStateCompat.STATE_PAUSED),
    IDLE(PlaybackStateCompat.STATE_NONE)
}