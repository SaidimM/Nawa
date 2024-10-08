package com.saidim.nawa.view.utils

import com.saidim.nawa.Constants
import com.saidim.nawa.R
import com.saidim.nawa.player.PlayerController

object Theming {
    fun getNotificationActionIcon(action: String, isNotification: Boolean): Int {
        val mediaPlayerHolder = PlayerController.getInstance()
        return when (action) {
            Constants.PLAY_PAUSE_ACTION -> if (mediaPlayerHolder.isPlaying()) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }
            Constants.PRE_ACTION -> R.drawable.ic_pre
            Constants.NEXT_ACTION -> R.drawable.ic_next
            Constants.CLOSE_ACTION -> R.drawable.ic_close
            else -> R.drawable.bg_corner_white_16dp
        }
    }
}